package ru.nti.dpts.schememanagerback.scheme.service.ops.equipment

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.ControlDto
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object AsynchronousMotorOps : EquipmentOps(EquipmentLibId.ASYNCHRONOUS_MOTOR) {

    override fun createControlsWithDefaultValuesAndBounds(fields: Map<FieldLibId, String?>):
        Map<ControlLibId, ControlDto> {
        return mapOf(
            createControlWithValueFromFieldAndNanBounds(
                ControlLibId.MOTOR_OPERATION_MODE,
                FieldLibId.MOTOR_OPERATION_MODE,
                fields
            ),
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.SPEED,
                FieldLibId.SPEED,
                fields
            ),
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.LOAD_MOMENT,
                FieldLibId.LOAD_MOMENT,
                fields
            )
        )
    }

    override fun getSubstationId(equipment: EquipmentNodeDomain): String {
        return equipment.getSubstationIdFromFields()
    }

    override fun getVoltageLevelInKilovolts(equipment: EquipmentNodeDomain): Double {
        return equipment.getEquipmentFieldDoubleValue(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE)
    }
}
