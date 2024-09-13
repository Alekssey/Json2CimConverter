package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.transformer

class TapChanger(
    val id: String,
    val defaultPosition: Int,
    val maxPosition: Int,
    val minPosition: Int,
    val voltageChange: Double
)
