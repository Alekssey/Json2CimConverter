package ru.nti.dtps.cimconverter.fromcim.extractor.voltage.basevoltage

import ru.nti.dtps.equipment.meta.info.dataclass.voltagelevel.VoltageLevelLib

class BaseVoltage(
    val id: String,
    val voltageLevelLib: VoltageLevelLib? = null
)
