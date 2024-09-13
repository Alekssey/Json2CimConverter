package ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout

data class DiagramObject(
    val id: String,
    val relatedObjectId: String,
    val hour: Int,
    val points: List<DiagramObjectPoint>
)
