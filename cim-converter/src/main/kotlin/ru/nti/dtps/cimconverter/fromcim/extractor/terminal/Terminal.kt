package ru.nti.dtps.cimconverter.fromcim.extractor.terminal

import ru.nti.dtps.cimconverter.rdf.schema.CimEnum

data class Terminal(
    val id: String,
    val equipmentId: String,
    val sequenceNumber: Int?,
    val phaseCode: CimEnum?,
    val connectivityNodeId: String? = null,
    val voltage: Voltage? = null
) {
    class Voltage(
        val magnitudeInKilovolts: Double,
        val angleInDegree: Double
    )
}
