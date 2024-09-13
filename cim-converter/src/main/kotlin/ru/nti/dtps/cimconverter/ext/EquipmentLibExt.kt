package ru.nti.dtps.cimconverter.ext

import ru.nti.dtps.equipment.meta.info.dataclass.equipment.EquipmentLib
import ru.nti.dtps.proto.lib.field.FieldLibId

fun EquipmentLib.hasFieldWithId(fieldLibId: FieldLibId): Boolean = fields.any { it.id == fieldLibId }
