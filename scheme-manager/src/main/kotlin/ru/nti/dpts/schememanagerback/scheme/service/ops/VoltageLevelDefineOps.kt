package ru.nti.dpts.schememanagerback.scheme.service.ops

import ru.nti.dtps.equipment.meta.info.dataclass.voltagelevel.VoltageLevelLib
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import kotlin.math.abs

fun findNearestVoltageLevelByVoltageInKilovolts(voltageInKilovolts: Double): VoltageLevelLib {
    val delta: (vl: VoltageLevelLib, vInKilovolts: Double) -> Double = { vl, vInKilovolts ->
        abs(vl.voltageInKilovolts - vInKilovolts)
    }

    return VoltageLevelLibId.values()
        .filter { it != VoltageLevelLibId.UNRECOGNIZED }
        .map { EquipmentMetaInfoManager.getVoltageLevelById(it) }
        .reduce { acc, vl ->
            if (delta(vl, voltageInKilovolts) < delta(acc, voltageInKilovolts)) {
                vl
            } else {
                acc
            }
        }
}
