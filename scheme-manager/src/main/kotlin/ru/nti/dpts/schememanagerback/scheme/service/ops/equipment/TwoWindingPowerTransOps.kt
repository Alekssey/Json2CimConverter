package ru.nti.dpts.schememanagerback.scheme.service.ops.equipment

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.ControlDto
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.PortDto
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.port.PortLibId

object TwoWindingPowerTransOps : EquipmentOps(EquipmentLibId.TWO_WINDING_POWER_TRANSFORMER) {
    override fun createControlsWithDefaultValuesAndBounds(fields: Map<FieldLibId, String?>):
        Map<ControlLibId, ControlDto> {
        return if (fields[FieldLibId.TAP_CHANGER_EXISTENCE] == "enabled") {
            val tapChangerMinPositionNumber = fields[FieldLibId.TAP_CHANGER_MIN_POSITION]!!.toDouble()
            val tapChangerMaxPositionNumber = fields[FieldLibId.TAP_CHANGER_MAX_POSITION]!!.toDouble()
            val tapChangerDefaultPosition = fields[FieldLibId.TAP_CHANGER_DEFAULT_POSITION]!!

            mapOf(
                ControlLibId.TAP_CHANGER_POSITION to ControlDto(
                    tapChangerDefaultPosition,
                    tapChangerMinPositionNumber,
                    tapChangerMaxPositionNumber
                )
            )
        } else {
            mapOf()
        }
    }

    fun getVoltageInKilovoltsByPort(equipment: EquipmentNodeDomain, port: PortDto): Double {
        return equipment.getEquipmentFieldDoubleValue(
            when (port.libId) {
                PortLibId.FIRST -> FieldLibId.FIRST_WINDING_RATED_VOLTAGE
                PortLibId.SECOND -> FieldLibId.SECOND_WINDING_RATED_VOLTAGE
                else -> throw java.lang.RuntimeException("Unexpected port library id ${port.libId}")
            }
        )
    }

    override fun getSubstationId(equipment: EquipmentNodeDomain): String {
        return equipment.getSubstationIdFromFields()
    }
}
