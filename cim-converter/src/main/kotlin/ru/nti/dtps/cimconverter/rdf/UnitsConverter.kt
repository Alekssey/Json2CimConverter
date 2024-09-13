package ru.nti.dtps.cimconverter.rdf

import ru.nti.dtps.cimconverter.rdf.schema.CimUnit

object UnitsConverter {

    fun withDefaultCimMultiplier(
        value: Double,
        cimUnit: CimUnit
    ): Double {
        return value / cimUnit.getDefaultMultiplier()
    }
}
