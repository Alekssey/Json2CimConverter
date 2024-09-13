package ru.nti.dpts.schememanagerback.scheme.service.ops.equipment

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object GroundDisconnector1PhOps : EquipmentOps(EquipmentLibId.GROUND_DISCONNECTOR_1PH) {
    override fun createControlsWithDefaultValuesAndBounds(fields: Map<FieldLibId, String?>):
        Map<ControlLibId, EquipmentNodeDomain.ControlDto> {
        return mapOf(
            ControlLibId.POSITION to EquipmentNodeDomain.ControlDto(
                if (fields[FieldLibId.POSITION] == "off") "disabled" else "enabled",
                Double.NaN,
                Double.NaN
            )
        )
    }

    override fun getSubstationId(equipment: EquipmentNodeDomain): String {
        return equipment.getSubstationIdFromFields()
    }
}
