package ru.nti.dpts.schememanagerback.scheme.service.ops.equipment

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object ElectricityStorageSystemOps : EquipmentOps(EquipmentLibId.ELECTRICITY_STORAGE_SYSTEM) {
    override fun createControlsWithDefaultValuesAndBounds(fields: Map<FieldLibId, String?>):
        Map<ControlLibId, EquipmentNodeDomain.ControlDto> {
        return mapOf(
            createControlWithValueFromFieldAndNanBounds(
                ControlLibId.ELECTRICITY_STORAGE_SYSTEM_MODE,
                FieldLibId.ELECTRICITY_STORAGE_SYSTEM_MODE,
                fields
            ),
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.ACTIVE_POWER_OF_CHARGE_DISCHARGE,
                FieldLibId.ACTIVE_POWER_OF_CHARGE_DISCHARGE,
                fields
            )
        )
    }

    override fun getSubstationId(equipment: EquipmentNodeDomain): String {
        return equipment.getSubstationIdFromFields()
    }
}
