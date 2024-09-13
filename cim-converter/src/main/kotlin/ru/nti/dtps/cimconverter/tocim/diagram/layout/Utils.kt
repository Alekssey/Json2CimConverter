package ru.nti.dtps.cimconverter.tocim.diagram.layout

fun convertHourToDegrees(hour: Int): Int = when (hour) {
    0 -> 0
    1 -> 90
    2 -> 180
    3 -> 270
    else -> throw IllegalArgumentException("Unexpected hour rotation: $hour")
}
