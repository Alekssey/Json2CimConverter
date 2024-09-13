package ru.nti.dtps.cimconverter.fromcim.extractor.link

import ru.nti.dtps.cimconverter.fromcim.extractor.terminal.Terminal
import ru.nti.dtps.dto.scheme.XyDto

data class PortInfo(
    val id: String,
    val equipmentId: String,
    val linkIds: List<String>,
    val sequenceNumber: Int?,
    val absoluteCoords: XyDto?,
    val terminal: Terminal? = null,
    val transmissionLinePointsCoords: List<XyDto>?
)
