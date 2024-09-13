package ru.nti.dpts.schememanagerback.scheme.service.ops.equipment

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.ControlDto
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object CurrentSourceDcOps : EquipmentOps(EquipmentLibId.CURRENT_SOURCE_DC) {
    override fun createControlsWithDefaultValuesAndBounds(fields: Map<FieldLibId, String?>): Map<ControlLibId, ControlDto> {
        return mapOf(
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.CURRENT_PHASE_A,
                FieldLibId.CURRENT_PHASE_A,
                fields
            ),
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.CURRENT_PHASE_B,
                FieldLibId.CURRENT_PHASE_B,
                fields
            ),
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.CURRENT_PHASE_C,
                FieldLibId.CURRENT_PHASE_C,
                fields
            ),
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.CONDUCTIVITY_PHASE_A,
                FieldLibId.CONDUCTIVITY_PHASE_A,
                fields
            ),
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.CONDUCTIVITY_PHASE_B,
                FieldLibId.CONDUCTIVITY_PHASE_B,
                fields
            ),
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.CONDUCTIVITY_PHASE_C,
                FieldLibId.CONDUCTIVITY_PHASE_C,
                fields
            )
        )
    }

    override fun getSubstationId(equipment: EquipmentNodeDomain): String {
        return equipment.getSubstationIdFromFields()
    }
}
