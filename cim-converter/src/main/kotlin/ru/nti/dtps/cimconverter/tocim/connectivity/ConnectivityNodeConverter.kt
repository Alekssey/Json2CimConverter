package ru.nti.dtps.cimconverter.tocim.connectivity

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.utils.graph.DtpsRawSchemeSearch
import java.util.*

object ConnectivityNodeConverter {

    fun convertAndReturnLinkIdToCnMap(
        scheme: RawSchemeDto,
        linkIdToTnMap: Map<String, RdfResource>
    ): Map<String, RdfResource> {
        val linkGroups: List<Set<RawEquipmentLinkDto>> = findLinkGroups(scheme)

        return createLinkIdToConnectivityNodeResourceMap(linkGroups, linkIdToTnMap)
    }

    private fun findLinkGroups(scheme: RawSchemeDto): List<Set<RawEquipmentLinkDto>> {
        val bfs = DtpsRawSchemeSearch.getBreadthFirstSearch(scheme) {
            it.libEquipmentId != EquipmentLibId.CONNECTIVITY && it.libEquipmentId != EquipmentLibId.BUS
        }

        return scheme.nodes.values
            .filter { it.libEquipmentId != EquipmentLibId.CONNECTIVITY && it.libEquipmentId != EquipmentLibId.BUS }
            .flatMap { equipment ->
                equipment.ports
                    .asSequence()
                    .filterNot(bfs::haveAllLinksFromPortBeenVisited)
                    .map { port ->
                        val linkGroup = mutableSetOf<RawEquipmentLinkDto>()

                        bfs.searchFromNodeAndSpecialPort(
                            equipment,
                            port,
                            doOnSiblingLink = { link -> linkGroup += link }
                        )

                        linkGroup
                    }
            }
    }

    private fun createLinkIdToConnectivityNodeResourceMap(
        linkGroups: List<Set<RawEquipmentLinkDto>>,
        linkIdToTnMap: Map<String, RdfResource>
    ): Map<String, RdfResource> {
        val linkIdToConnectivityNodeResourceMap = mutableMapOf<String, RdfResource>()
        linkGroups.map { group ->
            val resource = RdfResourceBuilder(
                UUID.randomUUID().toString(),
                CimClasses.ConnectivityNode
            ).let { builder ->
                group.firstOrNull()?.let(RawEquipmentLinkDto::id).let(linkIdToTnMap::get)?.let { tn ->
                    builder.addObjectProperty(CimClasses.ConnectivityNode.TopologicalNode, tn)
                }
                builder
            }.build()

            group.forEach { link -> linkIdToConnectivityNodeResourceMap[link.id] = resource }
        }
        return linkIdToConnectivityNodeResourceMap
    }
}
