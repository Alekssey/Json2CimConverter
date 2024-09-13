package ru.nti.dpts.schememanagerback.scheme.service.ops.equipment

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.ControlDto
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object VoltageTransformerOps : EquipmentOps(EquipmentLibId.VOLTAGE_TRANSFORMER) {
    override fun createControlsWithDefaultValuesAndBounds(fields: Map<FieldLibId, String?>): Map<ControlLibId, ControlDto> {
        return mapOf()
    }

    override fun getSubstationId(equipment: EquipmentNodeDomain): String {
        return equipment.getSubstationIdFromFields()
    }

    override fun getVoltageLevelInKilovolts(equipment: EquipmentNodeDomain): Double {
        return equipment.getEquipmentFieldDoubleValue(FieldLibId.FIRST_WINDING_RATED_VOLTAGE)
    }
}
