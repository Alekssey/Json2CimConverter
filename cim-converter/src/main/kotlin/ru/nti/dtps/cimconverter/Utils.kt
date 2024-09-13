package ru.nti.dtps.cimconverter

import kotlin.math.*

val radToDegree: (angleRad: Double) -> Double = { it * 180.0 / Math.PI }
val degreeToRad: (angleDegree: Double) -> Double = { it * Math.PI / 180.0 }

fun convertHourToDegrees(hour: Int): Int = hour * 90

fun convertDegreesToHour(degrees: Int): Int = degrees / 90

fun rotate(hour: Int, x0: Double, y0: Double, x: Double, y: Double): Pair<Double, Double> {
    val rad = hour * PI / 2
    val deltaX = x - x0
    val deltaY = y - y0

    return deltaX * cos(rad) - deltaY * sin(rad) + x0 to deltaX * sin(rad) + deltaY * cos(rad) + y0
}

fun rectangularToPolar(re: Double, im: Double): Pair<Double, Double> {
    val magnitude = sqrt(re * re + im * im)
    val angleRad = atan2(im, re)
    return magnitude to radToDegree(angleRad)
}
