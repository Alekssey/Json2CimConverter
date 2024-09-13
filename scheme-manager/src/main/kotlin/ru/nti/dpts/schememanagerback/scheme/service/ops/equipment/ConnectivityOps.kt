package ru.nti.dpts.schememanagerback.scheme.service.ops.equipment

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.ControlDto
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object ConnectivityOps : EquipmentOps(EquipmentLibId.CONNECTIVITY) {
    override fun createControlsWithDefaultValuesAndBounds(fields: Map<FieldLibId, String?>): Map<ControlLibId, ControlDto> {
        return mapOf()
    }
}
