package ru.nti.dtps.cimconverter.tocim.topological

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.dto.scheme.getFieldStringValue
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId.*
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.utils.graph.DtpsRawSchemeSearch
import java.util.*

object TopologicalNodeConverter {

    fun convertAndReturnLinkIdToTnMap(scheme: RawSchemeDto): Map<String, RdfResource> {
        val linkGroups: List<Set<RawEquipmentLinkDto>> = findLinkGroups(scheme)

        return createLinkIdToTopologicalNodeResourceMap(linkGroups)
    }

    private fun findLinkGroups(scheme: RawSchemeDto): List<Set<RawEquipmentLinkDto>> {
        val bfs = DtpsRawSchemeSearch.getBreadthFirstSearch(scheme, ::doesEquipmentHaveImpedance)

        return scheme.nodes.values
            .filter(::doesEquipmentHaveImpedance)
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

    private fun createLinkIdToTopologicalNodeResourceMap(
        linkGroups: List<Set<RawEquipmentLinkDto>>
    ): Map<String, RdfResource> {
        val linkIdToTopologicalNodeResourceMap = mutableMapOf<String, RdfResource>()
        linkGroups.map { group ->
            val resource = RdfResourceBuilder(
                UUID.randomUUID().toString(),
                CimClasses.TopologicalNode
            ).build()

            group.forEach { link ->
                linkIdToTopologicalNodeResourceMap[link.id] = resource
            }
        }
        return linkIdToTopologicalNodeResourceMap
    }

    private fun doesEquipmentHaveImpedance(equipment: RawEquipmentNodeDto) = when (equipment.libEquipmentId) {
        BUS, CONNECTIVITY, CURRENT_TRANSFORMER, GROUNDING, GROUNDING_1PH, VOLTAGE_TRANSFORMER, THREE_PHASE_CONNECTOR -> false
        CIRCUIT_BREAKER,
        CIRCUIT_BREAKER_1PH,
        DISCONNECTOR,
        DISCONNECTOR_1PH,
        GROUND_DISCONNECTOR,
        GROUND_DISCONNECTOR_1PH -> equipment.getFieldStringValue(FieldLibId.POSITION) != "on"

        TRANSMISSION_LINE_SEGMENT,
        TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT,
        POWER_SYSTEM_EQUIVALENT,
        TWO_WINDING_POWER_TRANSFORMER,
        THREE_WINDING_POWER_TRANSFORMER,
        LOAD,
        ASYNCHRONOUS_MOTOR,
        RESISTANCE,
        RESISTANCE_1PH,
        SHORT_CIRCUIT,
        SHORT_CIRCUIT_1PH,
        SYNCHRONOUS_GENERATOR,
        REACTIVE_POWER_COMPENSATION_SYSTEM,
        TWO_WINDING_AUTO_TRANSFORMER,
        THREE_WINDING_AUTO_TRANSFORMER,
        INDUCTANCE,
        INDUCTANCE_1PH,
        CAPACITANCE,
        CAPACITANCE_1PH,
        ELECTRICITY_STORAGE_SYSTEM,
        CURRENT_SOURCE_DC_1PH,
        CURRENT_SOURCE_DC,
        SOURCE_OF_ELECTROMOTIVE_FORCE_DC,
        SOURCE_OF_ELECTROMOTIVE_FORCE_DC_1PH -> true

        INDICATOR,
        VALUE,
        MEASUREMENT,
        BUTTON,

        UNRECOGNIZED -> throw IllegalStateException("Unrecognized equipment type")
    }
}
