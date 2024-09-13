package ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout

import ru.nti.dtps.dto.scheme.XyDto

class DiagramObjectPoint(
    val diagramObjectId: String,
    val sequenceNumber: Int?,
    val coords: XyDto
)
