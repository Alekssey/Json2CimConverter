package ru.nti.dtps.cimconverter.fromcim.extensions

import ru.nti.dtps.equipment.meta.info.dataclass.equipment.EquipmentLib
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

fun EquipmentLib.isTransmissionLineSegment(): Boolean {
    return this.id == EquipmentLibId.TRANSMISSION_LINE_SEGMENT
}

fun EquipmentLib.isBus(): Boolean {
    return this.id == EquipmentLibId.BUS
}

fun EquipmentLib.getDefaultEquipmentFields(): Map<FieldLibId, String?> {
    return fields.associate { field -> field.id to field.defaultValue }
}
