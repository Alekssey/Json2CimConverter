package ru.nti.dpts.schememanagerback.scheme.service.ops.equipment

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.ControlDto
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object StaticLoadOps : EquipmentOps(EquipmentLibId.LOAD) {
    override fun createControlsWithDefaultValuesAndBounds(fields: Map<FieldLibId, String?>):
        Map<ControlLibId, ControlDto> {
        return mapOf(
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.ACTIVE_POWER,
                FieldLibId.ACTIVE_POWER,
                fields
            ),
            createControlWithValueFromFieldAndDefaultBounds(
                ControlLibId.REACTIVE_POWER,
                FieldLibId.REACTIVE_POWER,
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
