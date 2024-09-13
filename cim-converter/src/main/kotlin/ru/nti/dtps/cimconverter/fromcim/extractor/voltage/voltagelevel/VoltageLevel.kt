package ru.nti.dtps.cimconverter.fromcim.extractor.voltage.voltagelevel

import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.basevoltage.BaseVoltage
import ru.nti.dtps.dto.scheme.RawSchemeDto

class VoltageLevel(
    val id: String,
    val substation: RawSchemeDto.SubstationDto ? = null,
    val baseVoltage: BaseVoltage? = null
)
