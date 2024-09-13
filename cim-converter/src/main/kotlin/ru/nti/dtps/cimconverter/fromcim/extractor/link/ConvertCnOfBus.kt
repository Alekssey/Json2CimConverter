package ru.nti.dtps.cimconverter.fromcim.extractor.link

import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObject
import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObjectPoint
import ru.nti.dtps.cimconverter.fromcim.extractor.terminal.Terminal
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.XyDto
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

internal fun convertConnectivityNodeOfBus(
    terminals: List<Terminal>,
    objectIdToDiagramObjectMap: Map<String, DiagramObject>,
    doesTerminalBelongToBus: (terminal: Terminal) -> Boolean
): LinksCreationResult {
    val (busTerminal, otherEquipmentsTerminals) = terminals
        .partition(doesTerminalBelongToBus)
        .let { (busTerminals, otherEquipmentsTerminals) -> busTerminals.first() to otherEquipmentsTerminals }

    val busDiagramObject = objectIdToDiagramObjectMap[busTerminal.equipmentId]

    val links = mutableListOf<RawEquipmentLinkDto>()
    val portsInfo = mutableListOf<PortInfo>()

    otherEquipmentsTerminals.forEach { otherTerminal ->
        val busPortId = UUID.randomUUID().toString()
        val otherTerminalDiagramObjectPoints = objectIdToDiagramObjectMap[otherTerminal.id]?.points ?: emptyList()

        val link = RawEquipmentLinkDto(
            id = UUID.randomUUID().toString(),
            source = busTerminal.equipmentId,
            sourcePort = busPortId,
            target = otherTerminal.equipmentId,
            targetPort = otherTerminal.id,
            points = otherTerminalDiagramObjectPoints.map(::mapPoint),
            voltageLevelId = null
        )
        val busPort = createPortInfo(
            portId = busPortId,
            equipmentId = busTerminal.equipmentId,
            link = link,
            sequenceNumber = null,
            coords = busDiagramObject?.let {
                findNearestBusDiagramObjectPoint(
                    otherTerminal,
                    busDiagramObject,
                    objectIdToDiagramObjectMap
                )?.coords
            } ?: XyDto(0.0, 0.0),
            pointsCoords = emptyList()
        )

        val equipmentPort = createPortInfo(otherTerminal, objectIdToDiagramObjectMap, link)

        links += link
        portsInfo += busPort
        portsInfo += equipmentPort
    }

    return LinksCreationResult(
        links = links,
        ports = portsInfo
    )
}

private fun findNearestBusDiagramObjectPoint(
    otherEquipmentTerminal: Terminal,
    busDiagramObject: DiagramObject,
    objectIdToDiagramObjectMap: Map<String, DiagramObject>
): DiagramObjectPoint? {
    val otherEquipmentTerminalObject = objectIdToDiagramObjectMap[otherEquipmentTerminal.id]

    return otherEquipmentTerminalObject?.points?.reduceOrNull { acc, diagramObjectPoint ->
        if (
            calculateMeanDistanceFromPointToOtherPoints(acc, busDiagramObject.points)
            <
            calculateMeanDistanceFromPointToOtherPoints(diagramObjectPoint, busDiagramObject.points)
        ) {
            acc
        } else {
            diagramObjectPoint
        }
    }
}

private fun calculateMeanDistanceFromPointToOtherPoints(
    point: DiagramObjectPoint,
    points: List<DiagramObjectPoint>
): Double {
    return if (points.isEmpty()) {
        0.0
    } else {
        var mean = 0.0
        points.forEach { mean += calculateDistance(it, point) }
        mean / points.size
    }
}

private fun calculateDistance(firstPoint: DiagramObjectPoint, secondPoint: DiagramObjectPoint): Double {
    return sqrt(
        (firstPoint.coords.x - secondPoint.coords.x).pow(2) +
            (firstPoint.coords.y - secondPoint.coords.y).pow(2)
    )
}
