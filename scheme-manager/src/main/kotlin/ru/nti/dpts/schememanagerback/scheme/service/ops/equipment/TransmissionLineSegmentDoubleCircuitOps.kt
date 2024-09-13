package ru.nti.dpts.schememanagerback.scheme.service.ops.equipment

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.ControlDto
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId

object TransmissionLineSegmentDoubleCircuitOps : EquipmentOps(EquipmentLibId.TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT) {
    override fun createControlsWithDefaultValuesAndBounds(fields: Map<FieldLibId, String?>): Map<ControlLibId, ControlDto> {
        return mapOf()
    }

    override fun getVoltageLevelInKilovolts(equipment: EquipmentNodeDomain): Double {
        return EquipmentMetaInfoManager.getVoltageLevelById(
            VoltageLevelLibId.valueOf(equipment.getEquipmentFieldStringValue(FieldLibId.VOLTAGE_LEVEL))
        ).voltageInKilovolts
    }

    override fun getTransmissionLineIds(equipment: EquipmentNodeDomain): List<String> {
        return equipment.getTransmissionLineIdFromFields()
    }
}
