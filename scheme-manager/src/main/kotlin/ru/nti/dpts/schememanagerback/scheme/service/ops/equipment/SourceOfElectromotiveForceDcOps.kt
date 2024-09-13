package ru.nti.dpts.schememanagerback.scheme.service.ops.equipment

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.ControlDto
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object SourceOfElectromotiveForceDcOps : EquipmentOps(EquipmentLibId.SOURCE_OF_ELECTROMOTIVE_FORCE_DC) {
    override fun createControlsWithDefaultValuesAndBounds(fields: Map<FieldLibId, String?>): Map<ControlLibId, ControlDto> {
        return mapOf(
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.ELECTROMOTIVE_FORCE_PHASE_A,
                FieldLibId.ELECTROMOTIVE_FORCE_PHASE_A,
                fields
            ),
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.ELECTROMOTIVE_FORCE_PHASE_B,
                FieldLibId.ELECTROMOTIVE_FORCE_PHASE_B,
                fields
            ),
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.ELECTROMOTIVE_FORCE_PHASE_C,
                FieldLibId.ELECTROMOTIVE_FORCE_PHASE_C,
                fields
            ),
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.RESISTANCE_PHASE_A,
                FieldLibId.RESISTANCE_PHASE_A,
                fields
            ),
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.RESISTANCE_PHASE_B,
                FieldLibId.RESISTANCE_PHASE_B,
                fields
            ),
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.RESISTANCE_PHASE_C,
                FieldLibId.RESISTANCE_PHASE_C,
                fields
            )
        )
    }

    override fun getSubstationId(equipment: EquipmentNodeDomain): String {
        return equipment.getSubstationIdFromFields()
    }

    override fun getVoltageLevelInKilovolts(equipment: EquipmentNodeDomain): Double {
        return equipment.getEquipmentFieldDoubleValue(FieldLibId.ELECTROMOTIVE_FORCE_PHASE_A)
    }
}
