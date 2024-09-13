package ru.nti.dpts.schememanagerback.scheme.service.ops.equipment

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.ControlDto
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object PowerSystemEquivalentOps : EquipmentOps(EquipmentLibId.POWER_SYSTEM_EQUIVALENT) {
    override fun createControlsWithDefaultValuesAndBounds(fields: Map<FieldLibId, String?>): Map<ControlLibId, ControlDto> {
        return mapOf(
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.VOLTAGE_LINE_TO_LINE,
                FieldLibId.VOLTAGE_LINE_TO_LINE,
                fields
            ),
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.ANGLE_OF_PHASE_A,
                FieldLibId.ANGLE_OF_PHASE_A,
                fields
            )
        )
    }

    override fun getVoltageLevelInKilovolts(equipment: EquipmentNodeDomain): Double {
        return equipment.getEquipmentFieldDoubleValue(FieldLibId.VOLTAGE_LINE_TO_LINE)
    }

    override fun getSubstationId(equipment: EquipmentNodeDomain): String {
        return equipment.getSubstationIdFromFields()
    }
}
