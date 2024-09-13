package ru.nti.dtps.cimconverter.ext

import ru.nti.dtps.proto.lib.equipment.EquipmentLibId

fun EquipmentLibId.isTwoWindingPowerTransformer(): Boolean {
    return this == EquipmentLibId.TWO_WINDING_POWER_TRANSFORMER
}

fun EquipmentLibId.isThreeWindingPowerTransformer(): Boolean {
    return this == EquipmentLibId.THREE_WINDING_POWER_TRANSFORMER
}
