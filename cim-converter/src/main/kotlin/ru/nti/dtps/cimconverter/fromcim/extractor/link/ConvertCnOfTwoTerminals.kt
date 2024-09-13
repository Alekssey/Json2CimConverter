package ru.nti.dtps.cimconverter.fromcim.extractor.link

import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObject
import ru.nti.dtps.cimconverter.fromcim.extractor.terminal.Terminal
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import java.util.*

internal fun convertConnectivityNodeOfTwoTerminals(
    terminals: List<Terminal>,
    objectIdToDiagramObjectMap: Map<String, DiagramObject>
): LinksCreationResult {
    val firstTerminal = terminals.first()
    val secondTerminal = terminals.last()

    val firstTerminalDiagramObject = objectIdToDiagramObjectMap[firstTerminal.id]
    val secondTerminalDiagramObject = objectIdToDiagramObjectMap[secondTerminal.id]

    val (terminalDiagramObjectPoints, sourceTerminal, targetTerminal) = when {
        firstTerminalDiagramObject != null && firstTerminalDiagramObject.points.size >= 2 -> Triple(
            firstTerminalDiagramObject.points,
            firstTerminal,
            secondTerminal
        )

        secondTerminalDiagramObject != null && secondTerminalDiagramObject.points.size >= 2 -> Triple(
            secondTerminalDiagramObject.points,
            secondTerminal,
            firstTerminal
        )

        else -> Triple(
            emptyList(),
            firstTerminal,
            secondTerminal
        )
    }

    val link = RawEquipmentLinkDto(
        id = UUID.randomUUID().toString(),
        source = sourceTerminal.equipmentId,
        sourcePort = sourceTerminal.id,
        target = targetTerminal.equipmentId,
        targetPort = targetTerminal.id,
        points = terminalDiagramObjectPoints.map(::mapPoint),
        voltageLevelId = null
    )

    val firstPort = createPortInfo(firstTerminal, objectIdToDiagramObjectMap, link)

    val secondPort = createPortInfo(secondTerminal, objectIdToDiagramObjectMap, link)

    return LinksCreationResult(
        links = listOf(link),
        ports = listOf(firstPort, secondPort)
    )
}
