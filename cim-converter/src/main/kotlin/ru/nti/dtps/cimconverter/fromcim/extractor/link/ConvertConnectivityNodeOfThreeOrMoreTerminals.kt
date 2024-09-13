package ru.nti.dtps.cimconverter.fromcim.extractor.link

import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObject
import ru.nti.dtps.cimconverter.fromcim.extractor.terminal.Terminal
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import java.util.*

internal fun convertConnectivityNodeOfThreeOrMoreTerminals(
    terminals: List<Terminal>,
    objectIdToDiagramObjectMap: Map<String, DiagramObject>
): LinksCreationResult {
    val linkIdToTerminalMap = terminals.associateBy { UUID.randomUUID().toString() }

    val connectivityId = UUID.randomUUID().toString()
    val connectivityPortId = UUID.randomUUID().toString()

    val connectivity = ConnectivityCreator.create(
        terminals,
        linkIdToTerminalMap.keys.toList(),
        connectivityId,
        connectivityPortId,
        objectIdToDiagramObjectMap
    )

    val linkIdToLinksMap = linkIdToTerminalMap.mapValues { (linkId, terminal) ->
        RawEquipmentLinkDto(
            id = linkId,
            source = terminal.equipmentId,
            sourcePort = terminal.id,
            target = connectivityId,
            targetPort = connectivityPortId,
            points = (objectIdToDiagramObjectMap[terminal.id]?.points ?: emptyList()).map(::mapPoint),
            voltageLevelId = null
        )
    }

    return LinksCreationResult(
        links = linkIdToLinksMap.values.toList(),
        ports = linkIdToTerminalMap.map { (linkId, terminal) ->
            createPortInfo(terminal, objectIdToDiagramObjectMap, linkIdToLinksMap[linkId]!!)
        },
        connectivities = listOf(connectivity)
    )
}
