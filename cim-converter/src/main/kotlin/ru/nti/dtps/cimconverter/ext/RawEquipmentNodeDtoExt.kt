package ru.nti.dtps.cimconverter.ext

import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto

fun RawEquipmentNodeDto.isTwoWindingPowerTransformer(): Boolean {
    return libEquipmentId.isTwoWindingPowerTransformer()
}

fun RawEquipmentNodeDto.isThreeWindingPowerTransformer(): Boolean {
    return libEquipmentId.isThreeWindingPowerTransformer()
}
