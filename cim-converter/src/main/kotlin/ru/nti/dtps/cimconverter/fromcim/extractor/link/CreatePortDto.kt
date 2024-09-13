package ru.nti.dtps.cimconverter.fromcim.extractor.link

import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObject
import ru.nti.dtps.cimconverter.fromcim.extractor.terminal.Terminal
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.XyDto

internal fun createPortInfo(
    terminal: Terminal,
    objectIdToDiagramObjectMap: Map<String, DiagramObject>,
    link: RawEquipmentLinkDto? = null
): PortInfo = createPortInfo(
    portId = terminal.id,
    equipmentId = terminal.equipmentId,
    link = link,
    sequenceNumber = terminal.sequenceNumber,
    coords = objectIdToDiagramObjectMap[terminal.id]?.points?.first()?.coords ?: XyDto(
        0.0,
        0.0
    ),
    pointsCoords = objectIdToDiagramObjectMap[terminal.id]?.points?.map { it.coords } ?: emptyList()
).copy(terminal = terminal)

internal fun createPortInfo(
    portId: String,
    equipmentId: String,
    sequenceNumber: Int? = null,
    link: RawEquipmentLinkDto? = null,
    coords: XyDto,
    pointsCoords: List<XyDto> = emptyList()
) = PortInfo(
    id = portId,
    equipmentId = equipmentId,
    linkIds = link?.let { listOf(it.id) } ?: listOf(),
    sequenceNumber = sequenceNumber,
    absoluteCoords = coords,
    transmissionLinePointsCoords = pointsCoords
)
