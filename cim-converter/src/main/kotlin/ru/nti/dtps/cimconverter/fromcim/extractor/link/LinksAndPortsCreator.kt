package ru.nti.dtps.cimconverter.fromcim.extractor.link

import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObject
import ru.nti.dtps.cimconverter.fromcim.extractor.terminal.Terminal
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIdentifiedObjectId
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto

object LinksAndPortsCreator {

    fun create(
        repository: RdfRepository,
        connectivityNodeIdToTerminalsMap: Map<String, List<Terminal>>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>
    ): Triple<List<RawEquipmentLinkDto>, Map<String, List<PortInfo>>, List<RawEquipmentNodeDto>> {
        val busIdSet = extractBusIdSet(repository)

        val doesTerminalBelongToBus: (terminal: Terminal) -> Boolean = { terminal ->
            busIdSet.contains(terminal.equipmentId)
        }

        return convert(connectivityNodeIdToTerminalsMap, objectIdToDiagramObjectMap, doesTerminalBelongToBus)
    }

    private fun convert(
        connectivityNodeIdToTerminalsMap: Map<String, List<Terminal>>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>,
        doesTerminalBelongToBus: (terminal: Terminal) -> Boolean
    ): Triple<List<RawEquipmentLinkDto>, Map<String, List<PortInfo>>, List<RawEquipmentNodeDto>> {
        val allLinks = mutableListOf<RawEquipmentLinkDto>()
        val equipmentIdToPortsMap = mutableMapOf<String, MutableList<PortInfo>>()
        val allConnectivities = mutableListOf<RawEquipmentNodeDto>()

        connectivityNodeIdToTerminalsMap.forEach { (_, terminals) ->
            val linksCreationResult = when {
                terminals.any(doesTerminalBelongToBus) -> convertConnectivityNodeOfBus(
                    terminals,
                    objectIdToDiagramObjectMap,
                    doesTerminalBelongToBus
                )

                terminals.isEmpty() -> convertEmptyConnectivityNode()
                terminals.size == 1 -> convertConnectivityNodeOfOneTerminal(terminals, objectIdToDiagramObjectMap)
                terminals.size == 2 -> convertConnectivityNodeOfTwoTerminals(terminals, objectIdToDiagramObjectMap)
                else -> convertConnectivityNodeOfThreeOrMoreTerminals(terminals, objectIdToDiagramObjectMap)
            }

            allLinks += linksCreationResult.links
            allConnectivities += linksCreationResult.connectivities
            linksCreationResult.ports.forEach { port ->
                equipmentIdToPortsMap.computeIfAbsent(port.equipmentId) { mutableListOf() } += port
            }
        }

        return Triple(allLinks, equipmentIdToPortsMap, allConnectivities)
    }

    private fun extractBusIdSet(repository: RdfRepository): Set<String> =
        repository.selectAllVarsFromTriples(CimClasses.BusbarSection).map { it.extractIdentifiedObjectId() }.toSet()
}
