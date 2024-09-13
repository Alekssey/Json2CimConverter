package ru.nti.dpts.schememanagerback.scheme.service.ops.equipment

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.ControlDto
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object SourceOfElectromotiveForceDc1PhOps : EquipmentOps(EquipmentLibId.SOURCE_OF_ELECTROMOTIVE_FORCE_DC_1PH) {
    override fun createControlsWithDefaultValuesAndBounds(fields: Map<FieldLibId, String?>): Map<ControlLibId, ControlDto> {
        return mapOf(
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.ELECTROMOTIVE_FORCE,
                FieldLibId.ELECTROMOTIVE_FORCE,
                fields
            ),
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.RESISTANCE,
                FieldLibId.RESISTANCE,
                fields
            )
        )
    }

    override fun getSubstationId(equipment: EquipmentNodeDomain): String {
        return equipment.getSubstationIdFromFields()
    }
}
