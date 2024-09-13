package ru.nti.dpts.schememanagerback.builder

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.*
import ru.nti.dpts.schememanagerback.scheme.domain.XyDto
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import ru.nti.dtps.proto.scheme.equipment.EquipmentNode.Port.Alignment
import java.util.*

data class EquipmentNodeBuilder(
    private var id: String = UUID.randomUUID().toString(),
    private var selected: Boolean = false,
    private var locked: Boolean = false,
    private var voltageLevelId: VoltageLevelLibId? = null,
    private var libEquipmentId: EquipmentLibId = EquipmentLibId.UNRECOGNIZED,
    private var hour: Int = 0,
    private var coords: XyDto = XyBuilder().build(),
    private var dimensions: SizeDto = SizeBuilder().build(),
    private var fields: MutableMap<FieldLibId, String?> = mutableMapOf(),
    private var controls: MutableMap<ControlLibId, ControlDto> = mutableMapOf(),
    private var ports: MutableList<PortDto> = mutableListOf()
) {

    data class XyBuilder(
        private var x: Double = 0.0,
        private var y: Double = 0.0
    ) {
        fun withX(x: Double) = apply { this.x = x }
        fun withY(y: Double) = apply { this.y = y }
        fun build() = XyDto(x, y)
    }

    data class SizeBuilder(
        private var height: Double = 0.0,
        private var width: Double = 0.0
    ) {
        fun withHeight(height: Double) = apply { this.height = height }
        fun withWidth(width: Double) = apply { this.width = width }
        fun build() = SizeDto(height, width)
    }

    data class ControlBuilder(
        private var value: String = "0",
        private var min: Double = 0.0,
        private var max: Double = 0.0
    ) {
        fun withValue(value: String) = apply { this.value = value }
        fun withMin(min: Double) = apply { this.min = min }
        fun withMax(max: Double) = apply { this.max = max }
        fun build() = ControlDto(value, min, max)
    }

    data class PortBuilder(
        private var id: String = UUID.randomUUID().toString(),
        private var libId: PortLibId = PortLibId.UNRECOGNIZED,
        private var locked: Boolean = false,
        private var selected: Boolean = false,
        private var parentNode: String = "",
        private var coords: XyDto = XyBuilder().build(),
        private var alignment: Alignment = Alignment.UNRECOGNIZED,
        private var links: MutableList<String> = mutableListOf(),
        private var points: MutableList<PointDto> = mutableListOf()
    ) {
        fun withId(id: String) = apply { this.id = id }
        fun withLibId(libId: PortLibId) = apply { this.libId = libId }
        fun withLocked(locked: Boolean) = apply { this.locked = locked }
        fun withSelected(selected: Boolean) = apply { this.selected = selected }
        fun withParentNode(parentNode: String) = apply { this.parentNode = parentNode }
        fun withCoords(coords: XyDto) = apply { this.coords = coords }
        fun withAlignment(alignment: Alignment) = apply { this.alignment = alignment }
        fun withLinks(links: List<String>) = apply { this.links.addAll(links) }
        fun withPoints(points: List<PointDto>) = apply { this.points.addAll(points) }
        fun build() = PortDto(
            id, libId, locked, selected, parentNode, coords, alignment, links, points
        )
    }

    data class PointBuilder(
        private var id: String = UUID.randomUUID().toString(),
        private var selected: Boolean = false,
        private var locked: Boolean = false,
        private var coords: XyDto = XyBuilder().build(),
        private var parentId: String = ""
    ) {
        fun withId(id: String) = apply { this.id = id }
        fun withSelected(selected: Boolean) = apply { this.selected = selected }
        fun withLocked(locked: Boolean) = apply { this.locked = locked }
        fun withCoords(coords: XyDto) = apply { this.coords = coords }
        fun withParentId(parentId: String) = apply { this.parentId = parentId }
        fun build() = PointDto(
            id,
            selected,
            locked,
            coords,
            parentId
        )
    }

    fun withId(id: String) = apply { this.id = id }
    fun withSelected(selected: Boolean) = apply { this.selected = selected }
    fun withLocked(locked: Boolean) = apply { this.locked = locked }
    fun withName(name: String) = apply { this.fields[FieldLibId.NAME] = name }
    fun withVoltageLevelId(voltageLevelId: VoltageLevelLibId?) = apply { this.voltageLevelId = voltageLevelId }
    fun withSubstationId(substationId: String) = apply { this.fields[FieldLibId.SUBSTATION] = substationId }
    fun withTransmissionLineId(transmissionLineId: String) = apply { this.fields[FieldLibId.TRANSMISSION_LINE] = transmissionLineId }
    fun withLibEquipmentId(libEquipmentId: EquipmentLibId) = apply { this.libEquipmentId = libEquipmentId }
    fun withHour(hour: Int) = apply { this.hour = hour }
    fun withCoords(coords: XyDto) = apply { this.coords = coords }
    fun withDimensions(dimensions: SizeDto) = apply { this.dimensions = dimensions }
    fun withFields(fields: Map<FieldLibId, String?>) = apply { this.fields.putAll(fields) }
    fun withControls(controls: Map<ControlLibId, ControlDto>) = apply { this.controls.putAll(controls) }
    fun withPorts(ports: List<PortDto>) = apply { this.ports.addAll(ports) }

    fun buildBus() = this.withName("Bus")
        .withLibEquipmentId(EquipmentLibId.BUS)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.BUS))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.BUS))

    fun buildTransmissionLineSegment() = this.withName("Transmission line segment")
        .withLibEquipmentId(EquipmentLibId.TRANSMISSION_LINE_SEGMENT)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.TRANSMISSION_LINE_SEGMENT))
        .withTransmissionLineId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.TRANSMISSION_LINE_SEGMENT))

    fun buildPowerSystemEquivalent() = this.withName("Power system equivalent")
        .withLibEquipmentId(EquipmentLibId.POWER_SYSTEM_EQUIVALENT)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.POWER_SYSTEM_EQUIVALENT))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.POWER_SYSTEM_EQUIVALENT))

    fun buildCircuitBreaker() = this.withName("Circuit breaker")
        .withLibEquipmentId(EquipmentLibId.CIRCUIT_BREAKER)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.CIRCUIT_BREAKER))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.CIRCUIT_BREAKER))

    fun buildTwoWindingPowerTransformer() = this.withName("Two winding power transformer")
        .withLibEquipmentId(EquipmentLibId.TWO_WINDING_POWER_TRANSFORMER)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.TWO_WINDING_POWER_TRANSFORMER))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.TWO_WINDING_POWER_TRANSFORMER))

    fun buildThreeWindingPowerTransformer() = this.withName("Three winding power transformer")
        .withLibEquipmentId(EquipmentLibId.THREE_WINDING_POWER_TRANSFORMER)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.THREE_WINDING_POWER_TRANSFORMER))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.THREE_WINDING_POWER_TRANSFORMER))

    fun buildTwoWindingAutoPowerTransformer() = this.withName("Two winding auto power transformer")
        .withLibEquipmentId(EquipmentLibId.TWO_WINDING_AUTO_TRANSFORMER)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.TWO_WINDING_AUTO_TRANSFORMER))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.TWO_WINDING_AUTO_TRANSFORMER))

    fun buildThreeWindingAutoPowerTransformer() = this.withName("Three winding auto power transformer")
        .withLibEquipmentId(EquipmentLibId.THREE_WINDING_AUTO_TRANSFORMER)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.THREE_WINDING_AUTO_TRANSFORMER))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.THREE_WINDING_AUTO_TRANSFORMER))

    fun buildLoad() = this.withName("Load")
        .withLibEquipmentId(EquipmentLibId.LOAD)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.LOAD))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.LOAD))

    fun buildAsynchronousMotor() = this.withName("Asynchronous motor")
        .withLibEquipmentId(EquipmentLibId.ASYNCHRONOUS_MOTOR)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.ASYNCHRONOUS_MOTOR))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.ASYNCHRONOUS_MOTOR))

    fun buildShortCircuit() = this.withName("Short circuit")
        .withLibEquipmentId(EquipmentLibId.SHORT_CIRCUIT)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.SHORT_CIRCUIT))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.SHORT_CIRCUIT))

    fun buildConnectivity() = this.withLibEquipmentId(EquipmentLibId.CONNECTIVITY)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.CONNECTIVITY))
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.CONNECTIVITY))

    fun buildGrounding() = this.withName("Grounding")
        .withLibEquipmentId(EquipmentLibId.GROUNDING)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.GROUNDING))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.GROUNDING))

    fun buildCurrentTransformer() = this.withName("Current transformer")
        .withLibEquipmentId(EquipmentLibId.CURRENT_TRANSFORMER)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.CURRENT_TRANSFORMER))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.CURRENT_TRANSFORMER))

    fun buildVoltageTransformer() = this.withName("Voltage transformer")
        .withLibEquipmentId(EquipmentLibId.VOLTAGE_TRANSFORMER)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.VOLTAGE_TRANSFORMER))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.VOLTAGE_TRANSFORMER))

    fun buildResistance() = this.withName("Resistance")
        .withLibEquipmentId(EquipmentLibId.RESISTANCE)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.RESISTANCE))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.RESISTANCE))

    fun buildSynchronousGenerator() = this.withName("Synchronous generator")
        .withLibEquipmentId(EquipmentLibId.SYNCHRONOUS_GENERATOR)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.SYNCHRONOUS_GENERATOR))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.SYNCHRONOUS_GENERATOR))

    fun buildReactivePowerCompSystem() = this.withName("Reactive power compensation system")
        .withLibEquipmentId(EquipmentLibId.REACTIVE_POWER_COMPENSATION_SYSTEM)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.REACTIVE_POWER_COMPENSATION_SYSTEM))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.REACTIVE_POWER_COMPENSATION_SYSTEM))

    fun buildInductance() = this.withName("Inductance")
        .withLibEquipmentId(EquipmentLibId.INDUCTANCE)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.INDUCTANCE))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.INDUCTANCE))

    fun buildCapacitance() = this.withName("Capacitance")
        .withLibEquipmentId(EquipmentLibId.CAPACITANCE)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.CAPACITANCE))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.CAPACITANCE))

    fun buildInductance1Ph() = this.withName("Inductance 1 ph")
        .withLibEquipmentId(EquipmentLibId.INDUCTANCE_1PH)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.INDUCTANCE_1PH))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.INDUCTANCE_1PH))

    fun buildCapacitance1Ph() = this.withName("Capacitance 1 ph")
        .withLibEquipmentId(EquipmentLibId.CAPACITANCE_1PH)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.CAPACITANCE_1PH))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.CAPACITANCE_1PH))

    fun buildCircuitBreaker1Ph() = this.withName("Circuit breaker 1 ph")
        .withLibEquipmentId(EquipmentLibId.CIRCUIT_BREAKER_1PH)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.CIRCUIT_BREAKER_1PH))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.CIRCUIT_BREAKER_1PH))

    fun buildGrounding1Ph() = this.withName("Grounding 1 ph")
        .withLibEquipmentId(EquipmentLibId.GROUNDING_1PH)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.GROUNDING_1PH))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.GROUNDING_1PH))

    fun buildResistance1Ph() = this.withName("Resistance 1 ph")
        .withLibEquipmentId(EquipmentLibId.RESISTANCE_1PH)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.RESISTANCE_1PH))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.RESISTANCE_1PH))

    fun buildShortCircuit1Ph() = this.withName("Short circuit 1 ph")
        .withLibEquipmentId(EquipmentLibId.SHORT_CIRCUIT_1PH)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.SHORT_CIRCUIT_1PH))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.SHORT_CIRCUIT_1PH))

    fun buildThreePhaseConnector() = this.withName("Three phase connector")
        .withLibEquipmentId(EquipmentLibId.THREE_PHASE_CONNECTOR)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.THREE_PHASE_CONNECTOR))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.THREE_PHASE_CONNECTOR))

    fun buildElectricityStorage() = this.withName("Electricity storage")
        .withLibEquipmentId(EquipmentLibId.ELECTRICITY_STORAGE_SYSTEM)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.ELECTRICITY_STORAGE_SYSTEM))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.ELECTRICITY_STORAGE_SYSTEM))

    fun buildDisconnector() = this.withName("Disconnector")
        .withLibEquipmentId(EquipmentLibId.DISCONNECTOR)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.DISCONNECTOR))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.DISCONNECTOR))

    fun buildDisconnector1Ph() = this.withName("Disconnector 1 ph")
        .withLibEquipmentId(EquipmentLibId.DISCONNECTOR_1PH)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.DISCONNECTOR_1PH))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.DISCONNECTOR_1PH))

    fun buildGroundDisconnector() = this.withName("Ground disconnector")
        .withLibEquipmentId(EquipmentLibId.GROUND_DISCONNECTOR)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.GROUND_DISCONNECTOR))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.GROUND_DISCONNECTOR))

    fun buildGroundDisconnector1Ph() = this.withName("Ground disconnector 1 ph")
        .withLibEquipmentId(EquipmentLibId.GROUND_DISCONNECTOR_1PH)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.GROUND_DISCONNECTOR_1PH))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.GROUND_DISCONNECTOR_1PH))

    fun buildCurrentSourceDC1Ph() = this.withName("Current source DC 1 ph")
        .withLibEquipmentId(EquipmentLibId.CURRENT_SOURCE_DC_1PH)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.CURRENT_SOURCE_DC_1PH))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.CURRENT_SOURCE_DC_1PH))

    fun buildSourceOfElectromotiveForceDC1Ph() = this.withName("Source of electromotive force DC 1 ph")
        .withLibEquipmentId(EquipmentLibId.SOURCE_OF_ELECTROMOTIVE_FORCE_DC_1PH)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.SOURCE_OF_ELECTROMOTIVE_FORCE_DC_1PH))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.SOURCE_OF_ELECTROMOTIVE_FORCE_DC_1PH))

    fun buildSourceOfElectromotiveForceDC() = this.withName("Source of electromotive force DC")
        .withLibEquipmentId(EquipmentLibId.SOURCE_OF_ELECTROMOTIVE_FORCE_DC)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.SOURCE_OF_ELECTROMOTIVE_FORCE_DC))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.SOURCE_OF_ELECTROMOTIVE_FORCE_DC))

    fun buildCurrentSourceDC() = this.withName("Current source DC")
        .withLibEquipmentId(EquipmentLibId.CURRENT_SOURCE_DC)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.CURRENT_SOURCE_DC))
        .withSubstationId("noId")
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.CURRENT_SOURCE_DC))

    fun buildTransmissionLineDoubleSegment() = this.withName("Transmission line double segment")
        .withLibEquipmentId(EquipmentLibId.TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT)
        .withFields(getDefaultFieldsByEquipmentLibId(EquipmentLibId.TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT))
        .withControls(getDefaultControlsByEquipmentLibId(EquipmentLibId.TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT))

    private fun getDefaultFieldsByEquipmentLibId(equipmentLibId: EquipmentLibId): Map<FieldLibId, String?> {
        val fields = mutableMapOf<FieldLibId, String?>()
        val equipmentLib = EquipmentMetaInfoManager.getEquipmentLibById(equipmentLibId)
        equipmentLib.fields.forEach { fieldLib -> fields[fieldLib.id] = fieldLib.defaultValue }
        return fields
    }

    private fun getDefaultControlsByEquipmentLibId(equipmentLibId: EquipmentLibId): Map<ControlLibId, ControlDto> {
        val controls = mutableMapOf<ControlLibId, ControlDto>()
        val equipmentLib = EquipmentMetaInfoManager.getEquipmentLibById(equipmentLibId)
        equipmentLib.controls.forEach { controlLib ->
            run {
                val controlDto = ControlBuilder()
                    .withValue(controlLib.defaultValue ?: "0")
                    .withMin(controlLib.min ?: 0.0)
                    .withMax(controlLib.max ?: 0.0)
                    .build()
                controls[controlLib.id] = controlDto
            }
        }
        return controls
    }

    fun build() = EquipmentNodeDomain(
        id,
        selected,
        locked,
        voltageLevelId,
        libEquipmentId,
        hour,
        coords,
        dimensions,
        fields,
        controls,
        ports
    )
}
