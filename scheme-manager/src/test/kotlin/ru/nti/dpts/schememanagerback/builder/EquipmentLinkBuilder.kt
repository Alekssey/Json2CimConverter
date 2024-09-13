package ru.nti.dpts.schememanagerback.builder

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentLinkDomain.PointDto
import ru.nti.dpts.schememanagerback.scheme.domain.XyDto
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import ru.nti.dtps.proto.scheme.link.EquipmentLink.AlignmentType
import java.util.UUID

data class EquipmentLinkBuilder(
    private var id: String = UUID.randomUUID().toString(),
    private var alignmentType: AlignmentType = AlignmentType.RECTANGULAR,
    private var locked: Boolean = false,
    private var selected: Boolean = false,
    private var source: String = "",
    private var target: String = "",
    private var sourcePort: String = "",
    private var targetPort: String = "",
    private var voltageLevelId: VoltageLevelLibId? = null,
    private var points: MutableList<PointDto> = mutableListOf()
) {
    data class PointBuilder(
        private var id: String = UUID.randomUUID().toString(),
        private var locked: Boolean = false,
        private var selected: Boolean = false,
        private var coords: XyDto = EquipmentNodeBuilder.XyBuilder().build()
    ) {
        fun withId(id: String) = apply { this.id = id }
        fun withLocked(locked: Boolean) = apply { this.locked = locked }
        fun withSelected(selected: Boolean) = apply { this.selected = selected }
        fun withCoords(coords: XyDto) = apply { this.coords = coords }
        fun build() = PointDto(
            id,
            locked,
            selected,
            coords
        )
    }

    fun withId(id: String) = apply { this.id = id }
    fun withAlignmentType(alignmentType: AlignmentType) = apply { this.alignmentType = alignmentType }
    fun withLocked(locked: Boolean) = apply { this.locked = locked }
    fun withSelected(selected: Boolean) = apply { this.selected = selected }
    fun withSource(source: String) = apply { this.source = source }
    fun withTarget(target: String) = apply { this.target = target }
    fun withSourcePort(sourcePort: String) = apply { this.sourcePort = sourcePort }
    fun withTargetPort(targetPort: String) = apply { this.targetPort = targetPort }
    fun withVoltageLeveId(voltageLevelId: VoltageLevelLibId?) = apply { this.voltageLevelId = voltageLevelId }
    fun withPoints(points: List<PointDto>) = apply { this.points.addAll(points) }
    fun build() = ru.nti.dpts.schememanagerback.scheme.domain.EquipmentLinkDomain(
        id,
        alignmentType,
        locked,
        selected,
        source,
        target,
        sourcePort,
        targetPort,
        voltageLevelId,
        points
    )
}
