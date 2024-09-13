package ru.nti.dpts.schememanagerback.scheme.service.ops.equipment

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object SynchronousGeneratorOps : EquipmentOps(EquipmentLibId.SYNCHRONOUS_GENERATOR) {
    override fun createControlsWithDefaultValuesAndBounds(fields: Map<FieldLibId, String?>): Map<ControlLibId, EquipmentNodeDomain.ControlDto> {
        val defaultControls = mutableMapOf(
            createControlWithDefaultValueAndDefaultBoundsFromControlLib(ControlLibId.VOLTAGE),
            createControlWithDefaultValueAndDefaultBoundsFromControlLib(ControlLibId.DRIVING_ELECTROMOTIVE_FORCE),
            createControlWithDefaultValueAndDefaultBoundsFromControlLib(ControlLibId.SPEED),
            createControlWithDefaultValueAndDefaultBoundsFromControlLib(ControlLibId.MOMENT),
            createControlWithDefaultValueAndDefaultBoundsFromControlLib(ControlLibId.POWER)
        )

        if (fields[FieldLibId.DRIVING_WINDING_AUTOMATIC_REGULATION] == "disabled") {
            defaultControls += createControlWithValueFromFieldAndNanBounds(
                ControlLibId.DRIVING_WINDING_AUTOMATIC_REGULATION,
                FieldLibId.DRIVING_WINDING_AUTOMATIC_REGULATION,
                fields
            )
        } else {
            defaultControls += createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.VOLTAGE,
                FieldLibId.VOLTAGE_SETPOINT,
                fields
            )
            defaultControls += createControlWithValueFromFieldAndNanBounds(
                ControlLibId.DRIVING_WINDING_AUTOMATIC_REGULATION,
                FieldLibId.DEFAULT_DRIVING_WINDING_AUTOMATIC_REGULATION_STATE,
                fields
            )
        }

        if (fields[FieldLibId.SPEED_AUTOMATIC_REGULATION] == "disabled") {
            defaultControls += createControlWithValueFromFieldAndNanBounds(
                ControlLibId.SPEED_AUTOMATIC_REGULATION,
                FieldLibId.SPEED_AUTOMATIC_REGULATION,
                fields
            )
        } else {
            defaultControls += createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.SPEED,
                FieldLibId.FREQUENCY_SETPOINT,
                fields
            )
            defaultControls += createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.MOMENT,
                FieldLibId.TURBINE_POWER_PU,
                fields
            )
            defaultControls += createControlWithValueFromFieldAndNanBounds(
                ControlLibId.SPEED_AUTOMATIC_REGULATION,
                FieldLibId.DEFAULT_SPEED_AUTOMATIC_REGULATION_STATE,
                fields
            )
        }

        if (fields[FieldLibId.POWER_AUTOMATIC_REGULATION] == "disabled") {
            defaultControls += createControlWithValueFromFieldAndNanBounds(
                ControlLibId.POWER_AUTOMATIC_REGULATION,
                FieldLibId.POWER_AUTOMATIC_REGULATION,
                fields
            )
        } else {
            defaultControls += createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.POWER,
                FieldLibId.POWER_SETPOINT,
                fields
            )
            defaultControls += createControlWithValueFromFieldAndNanBounds(
                ControlLibId.POWER_AUTOMATIC_REGULATION,
                FieldLibId.DEFAULT_AUTOMATIC_SYSTEM_STATE,
                fields
            )
        }

        return defaultControls
    }

    override fun getVoltageLevelInKilovolts(equipment: EquipmentNodeDomain): Double {
        return equipment.getEquipmentFieldDoubleValue(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE)
    }

    override fun getSubstationId(equipment: EquipmentNodeDomain): String {
        return equipment.getSubstationIdFromFields()
    }
}
