package ru.nti.dpts.schememanagerback.scheme.domain

import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import ru.nti.dtps.proto.scheme.equipment.EquipmentNode.Port.Alignment

data class EquipmentNodeDomain(
    var id: String,
    val selected: Boolean = false,
    val locked: Boolean = false,
    var voltageLevelId: VoltageLevelLibId?,
    val libEquipmentId: EquipmentLibId,
    val hour: Int = 0,
    val coords: XyDto,
    val dimensions: SizeDto,
    val fields: Map<FieldLibId, String?> = mapOf(),
    var controls: Map<ControlLibId, ControlDto> = mapOf(),
    val ports: List<PortDto> = listOf()
) {
    data class PortDto(
        val id: String,
        val libId: PortLibId,
        val locked: Boolean = false,
        val selected: Boolean = false,
        val parentNode: String,
        val coords: XyDto,
        val alignment: Alignment,
        val links: MutableList<String> = mutableListOf(),
        val points: MutableList<PointDto> = mutableListOf()
    )

    data class SizeDto(
        val height: Double = 0.0,
        val width: Double = 0.0
    )

    data class ControlDto(
        val value: String,
        val min: Double = 0.0,
        val max: Double = 0.0
    )

    data class PointDto(
        val id: String,
        val selected: Boolean = false,
        val locked: Boolean = false,
        val coords: XyDto,
        val parentId: String
    )
}
