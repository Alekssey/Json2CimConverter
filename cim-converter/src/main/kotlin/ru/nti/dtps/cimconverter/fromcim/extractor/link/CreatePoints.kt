package ru.nti.dtps.cimconverter.fromcim.extractor.link

import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObjectPoint
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.XyDto
import java.util.*

fun mapPoint(
    diagramObjectPoint: DiagramObjectPoint
) = RawEquipmentLinkDto.PointDto(
    id = UUID.randomUUID().toString(),
    coords = XyDto(diagramObjectPoint.coords.x, diagramObjectPoint.coords.y)
)
