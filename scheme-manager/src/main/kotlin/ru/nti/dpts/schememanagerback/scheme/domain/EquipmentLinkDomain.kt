package ru.nti.dpts.schememanagerback.scheme.domain

import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import ru.nti.dtps.proto.scheme.link.EquipmentLink.AlignmentType

data class EquipmentLinkDomain(
    val id: String,
    val alignmentType: AlignmentType = AlignmentType.RECTANGULAR,
    val locked: Boolean = false,
    val selected: Boolean = false,
    val source: String,
    val target: String,
    val sourcePort: String,
    val targetPort: String,
    var voltageLevelId: VoltageLevelLibId? = null,
    val points: List<PointDto> = listOf()
) {
    data class PointDto(
        val id: String,
        val locked: Boolean = false,
        val selected: Boolean = false,
        val coords: XyDto
    )
}
