package ru.nti.dpts.schememanagerback.scheme.service.ops.equipment

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object ValueOps : EquipmentOps(EquipmentLibId.VALUE) {
    override fun createControlsWithDefaultValuesAndBounds(
        fields: Map<FieldLibId, String?>
    ): Map<ControlLibId, EquipmentNodeDomain.ControlDto> {
        return mapOf(
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.VALUE,
                FieldLibId.VALUE,
                fields
            )
        )
    }
}
