package ru.nti.dpts.schememanagerback.scheme.service.ops.equipment

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.ControlDto
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object ReactivePowerCompSystemOps : EquipmentOps(EquipmentLibId.REACTIVE_POWER_COMPENSATION_SYSTEM) {

    override fun createControlsWithDefaultValuesAndBounds(fields: Map<FieldLibId, String?>):
        Map<ControlLibId, ControlDto> {
        return mapOf(
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.VOLTAGE,
                FieldLibId.VOLTAGE_SETPOINT,
                fields
            )
        )
    }

    override fun getVoltageLevelInKilovolts(equipment: EquipmentNodeDomain): Double {
        return equipment.getEquipmentFieldDoubleValue(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE)
    }

    override fun getSubstationId(equipment: EquipmentNodeDomain): String {
        return equipment.getSubstationIdFromFields()
    }
}
