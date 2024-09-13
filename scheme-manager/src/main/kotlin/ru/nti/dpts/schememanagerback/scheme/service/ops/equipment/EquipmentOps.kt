package ru.nti.dpts.schememanagerback.scheme.service.ops.equipment

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentLinkDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.ControlDto
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.PortDto
import ru.nti.dtps.equipment.meta.info.dataclass.equipment.EquipmentLib
import ru.nti.dtps.equipment.meta.info.dataclass.equipment.field.FieldValueType
import ru.nti.dtps.equipment.meta.info.extension.isTransmissionLine
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

private const val NO_ID = "noId"

abstract class EquipmentOps(equipmentLibId: EquipmentLibId) {

    private val equipmentLib: EquipmentLib = EquipmentMetaInfoManager.getEquipmentLibById(equipmentLibId)

    abstract fun createControlsWithDefaultValuesAndBounds(fields: Map<FieldLibId, String?>): Map<ControlLibId, ControlDto>
    open fun getSubstationId(equipment: EquipmentNodeDomain): String = NO_ID
    open fun getTransmissionLineIds(equipment: EquipmentNodeDomain): List<String> = listOf(NO_ID)
    open fun getVoltageLevelInKilovolts(equipment: EquipmentNodeDomain): Double? = null

    protected fun createControlWithValueFromFieldAndDefaultBounds(
        controlLibId: ControlLibId,
        fieldLibId: FieldLibId,
        fields: Map<FieldLibId, String?>
    ): Pair<ControlLibId, ControlDto> {
        return controlLibId to ControlDto(
            fields[fieldLibId]!!,
            getDefaultControlMinValue(controlLibId),
            getDefaultControlMaxValue(controlLibId)
        )
    }

    protected fun createControlWithValueFromFieldAndNanBounds(
        controlLibId: ControlLibId,
        fieldLibId: FieldLibId,
        fields: Map<FieldLibId, String?>
    ): Pair<ControlLibId, ControlDto> {
        return controlLibId to ControlDto(
            fields[fieldLibId]!!,
            Double.NaN,
            Double.NaN
        )
    }

    protected fun createControlWithDefaultValueAndDefaultBounds(
        controlLibId: ControlLibId,
        defaultValue: String
    ): Pair<ControlLibId, ControlDto> {
        return controlLibId to ControlDto(
            defaultValue,
            getDefaultControlMinValue(controlLibId),
            getDefaultControlMaxValue(controlLibId)
        )
    }

    protected fun createControlWithDefaultValueAndNanBounds(
        controlLibId: ControlLibId
    ): Pair<ControlLibId, ControlDto> {
        val controlLib = EquipmentMetaInfoManager
            .getControlLibByEquipmentLibIdAndControlLibId(equipmentLib.id, controlLibId).also {
                if (EquipmentMetaInfoManager.getFieldTypeById(it.typeId).valueType == FieldValueType.NUMBER) {
                    throw RuntimeException("Control with value type NUMBER must have number min and max, but not NaN")
                }
            }
        return controlLibId to ControlDto(
            controlLib.defaultValue ?: throw RuntimeException(
                "Default value for control lib id $controlLibId " +
                    "is not specified"
            ),
            Double.NaN,
            Double.NaN
        )
    }

    protected fun createControlWithDefaultValueAndDefaultBoundsFromControlLib(
        controlLibId: ControlLibId
    ): Pair<ControlLibId, ControlDto> {
        val controlLib = EquipmentMetaInfoManager
            .getControlLibByEquipmentLibIdAndControlLibId(equipmentLib.id, controlLibId)
        return controlLibId to ControlDto(
            controlLib.defaultValue ?: throw RuntimeException(
                "Default value for control lib id $controlLibId " +
                    "is not specified"
            ),
            getDefaultControlMinValue(controlLibId),
            getDefaultControlMaxValue(controlLibId)
        )
    }

    private fun getDefaultControlMinValue(controlLibId: ControlLibId): Double {
        val controlLib = EquipmentMetaInfoManager
            .getControlLibByEquipmentLibIdAndControlLibId(equipmentLib.id, controlLibId)
        return controlLib.min ?: EquipmentMetaInfoManager.getFieldTypeById(controlLib.typeId).also { fieldType ->
            if (fieldType.valueType != FieldValueType.NUMBER) {
                throw RuntimeException("Only NUMBER control value type has min value")
            }
        }.min!!
    }

    private fun getDefaultControlMaxValue(controlLibId: ControlLibId): Double {
        val controlLib = EquipmentMetaInfoManager
            .getControlLibByEquipmentLibIdAndControlLibId(equipmentLib.id, controlLibId)
        return controlLib.max ?: EquipmentMetaInfoManager.getFieldTypeById(controlLib.typeId).also { fieldType ->
            if (fieldType.valueType != FieldValueType.NUMBER) {
                throw RuntimeException("Only NUMBER control value type has max value")
            }
        }.max!!
    }
}

fun EquipmentNodeDomain.getFieldValueOrDefault(fieldLibId: FieldLibId): String? {
    val fieldLib = EquipmentMetaInfoManager.getFieldByEquipmentLibIdAndFieldLibId(
        this.libEquipmentId,
        fieldLibId
    )
    return this.fields[fieldLibId] ?: fieldLib.defaultValue
}
fun EquipmentNodeDomain.getFieldValueOrNull(fieldLibId: FieldLibId): String? {
    return this.fields[fieldLibId]
}

fun EquipmentNodeDomain.getRelatedPort(
    link: EquipmentLinkDomain
): PortDto {
    return this.ports.first { port -> port.links.contains(link.id) }
}

fun EquipmentNodeDomain.getEquipmentFieldDoubleValue(
    fieldLibId: FieldLibId
): Double {
    return this.getEquipmentFieldStringValue(fieldLibId).toDouble()
}

fun EquipmentNodeDomain.getEquipmentFieldStringValue(
    fieldLibId: FieldLibId
): String {
    return this.fields[fieldLibId] ?: throw IllegalArgumentException(
        "Equipment ${this.libEquipmentId} doesn't have field $fieldLibId"
    )
}

fun EquipmentNodeDomain.getName(): String {
    return this.fields[FieldLibId.NAME] ?: throw IllegalArgumentException(
        "Equipment ${this.libEquipmentId} doesn't have field ${FieldLibId.NAME}"
    )
}

fun EquipmentNodeDomain.getNameOrId() =
    if (this.libEquipmentId.isEquipmentWithName()) {
        this.getName()
    } else {
        this.id
    }

fun EquipmentNodeDomain.getSubstationIdFromFields(): String {
    return this.fields[FieldLibId.SUBSTATION] ?: throw IllegalArgumentException(
        "Equipment ${this.libEquipmentId} doesn't have field ${FieldLibId.SUBSTATION}"
    )
}

fun EquipmentNodeDomain.getTransmissionLineIdFromFields(): List<String> {
    val transmissionLineIds: MutableList<String> = mutableListOf()
    if (this.libEquipmentId.isTransmissionLine()) {
        val transmissionLineId = this.fields[FieldLibId.TRANSMISSION_LINE] ?: throw IllegalArgumentException(
            "Equipment ${this.libEquipmentId} doesn't have field ${FieldLibId.TRANSMISSION_LINE}"
        )
        transmissionLineIds.add(transmissionLineId)
    } else if (this.libEquipmentId == EquipmentLibId.TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT) {
        val transmissionLineIdFirstCircuit = this.fields[FieldLibId.FIRST_CIRCUIT_TRANSMISSION_LINE]
            ?: throw IllegalArgumentException(
                "Equipment ${this.libEquipmentId} doesn't have field ${FieldLibId.FIRST_CIRCUIT_TRANSMISSION_LINE}"
            )

        val transmissionLineIdSecondCircuit = this.fields[FieldLibId.SECOND_CIRCUIT_TRANSMISSION_LINE]
            ?: throw IllegalArgumentException(
                "Equipment ${this.libEquipmentId} doesn't have field ${FieldLibId.SECOND_CIRCUIT_TRANSMISSION_LINE}"
            )
        transmissionLineIds.addAll(listOf(transmissionLineIdFirstCircuit, transmissionLineIdSecondCircuit))
    }
    return transmissionLineIds
}

fun EquipmentNodeDomain.getVoltageInKilovolts(): Double {
    return getVoltageInKilovoltsOrNull() ?: throw IllegalArgumentException(
        "Equipment ${this.id} (${this.libEquipmentId}) should have voltage level"
    )
}

fun EquipmentNodeDomain.isThereVoltageLevel(): Boolean {
    return getVoltageInKilovoltsOrNull() != null
}

fun EquipmentNodeDomain.getVoltageInKilovoltsByTransformerType(powerTransformerPort: PortDto): Double {
    return when (this.libEquipmentId) {
        EquipmentLibId.TWO_WINDING_POWER_TRANSFORMER -> TwoWindingPowerTransOps.getVoltageInKilovoltsByPort(
            this,
            powerTransformerPort
        )

        EquipmentLibId.THREE_WINDING_POWER_TRANSFORMER -> ThreeWindingPowerTransOps.getVoltageInKilovoltsByPort(
            this,
            powerTransformerPort
        )

        EquipmentLibId.TWO_WINDING_AUTO_TRANSFORMER -> TwoWindingAutoPowerTransOps.getVoltageInKilovoltsByPort(
            this,
            powerTransformerPort
        )

        EquipmentLibId.THREE_WINDING_AUTO_TRANSFORMER -> ThreeWindingAutoPowerTransOps.getVoltageInKilovoltsByPort(
            this,
            powerTransformerPort
        )

        else -> Double.NaN
    }
}

val selectOps: (equipmentLibId: EquipmentLibId) -> EquipmentOps = {
    when (it) {
        EquipmentLibId.TWO_WINDING_POWER_TRANSFORMER -> TwoWindingPowerTransOps
        EquipmentLibId.THREE_WINDING_POWER_TRANSFORMER -> ThreeWindingPowerTransOps
        EquipmentLibId.TWO_WINDING_AUTO_TRANSFORMER -> TwoWindingAutoPowerTransOps
        EquipmentLibId.THREE_WINDING_AUTO_TRANSFORMER -> ThreeWindingAutoPowerTransOps
        EquipmentLibId.CIRCUIT_BREAKER -> CircuitBreakerOps
        EquipmentLibId.BUS -> BusOps
        EquipmentLibId.TRANSMISSION_LINE_SEGMENT -> TransmissionLineSegmentOps
        EquipmentLibId.POWER_SYSTEM_EQUIVALENT -> PowerSystemEquivalentOps
        EquipmentLibId.LOAD -> StaticLoadOps
        EquipmentLibId.ASYNCHRONOUS_MOTOR -> AsynchronousMotorOps
        EquipmentLibId.SHORT_CIRCUIT -> ShortCircuitOps
        EquipmentLibId.CONNECTIVITY -> ConnectivityOps
        EquipmentLibId.GROUNDING -> GroundingOps
        EquipmentLibId.CURRENT_TRANSFORMER -> CurrentTransformerOps
        EquipmentLibId.VOLTAGE_TRANSFORMER -> VoltageTransformerOps
        EquipmentLibId.RESISTANCE -> ResistanceOps
        EquipmentLibId.SYNCHRONOUS_GENERATOR -> SynchronousGeneratorOps
        EquipmentLibId.REACTIVE_POWER_COMPENSATION_SYSTEM -> ReactivePowerCompSystemOps
        EquipmentLibId.INDUCTANCE -> InductanceOps
        EquipmentLibId.CAPACITANCE -> CapacitanceOps
        EquipmentLibId.INDUCTANCE_1PH -> Inductance1PhOps
        EquipmentLibId.CAPACITANCE_1PH -> Capacitance1PhOps
        EquipmentLibId.CIRCUIT_BREAKER_1PH -> CircuitBreaker1PhOps
        EquipmentLibId.GROUNDING_1PH -> Grounding1PhOps
        EquipmentLibId.RESISTANCE_1PH -> Resistance1PhOps
        EquipmentLibId.SHORT_CIRCUIT_1PH -> ShortCircuit1PhOps
        EquipmentLibId.THREE_PHASE_CONNECTOR -> ThreePhaseConnectorOps
        EquipmentLibId.ELECTRICITY_STORAGE_SYSTEM -> ElectricityStorageSystemOps
        EquipmentLibId.DISCONNECTOR -> DisconnectorOps
        EquipmentLibId.DISCONNECTOR_1PH -> Disconnector1PhOps
        EquipmentLibId.GROUND_DISCONNECTOR -> GroundDisconnectorOps
        EquipmentLibId.GROUND_DISCONNECTOR_1PH -> GroundDisconnector1PhOps
        EquipmentLibId.CURRENT_SOURCE_DC_1PH -> CurrentSourceDc1PhOps
        EquipmentLibId.SOURCE_OF_ELECTROMOTIVE_FORCE_DC_1PH -> SourceOfElectromotiveForceDc1PhOps
        EquipmentLibId.MEASUREMENT -> MeasurementOps
        EquipmentLibId.VALUE -> ValueOps
        EquipmentLibId.BUTTON -> ButtonOps
        EquipmentLibId.INDICATOR -> IndicatorOps
        EquipmentLibId.CURRENT_SOURCE_DC -> CurrentSourceDcOps
        EquipmentLibId.SOURCE_OF_ELECTROMOTIVE_FORCE_DC -> SourceOfElectromotiveForceDcOps
        EquipmentLibId.TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT -> TransmissionLineSegmentDoubleCircuitOps
        EquipmentLibId.UNRECOGNIZED -> throw IllegalArgumentException("No such type of equipment ${it.name}")
    }
}

private fun EquipmentNodeDomain.getVoltageInKilovoltsOrNull(): Double? {
    return selectOps(this.libEquipmentId).getVoltageLevelInKilovolts(this)
}

fun EquipmentNodeDomain.getSubstationId(): String {
    return selectOps(this.libEquipmentId).getSubstationId(this)
}

fun EquipmentNodeDomain.getTransmissionLineIds(): List<String> {
    return selectOps(this.libEquipmentId).getTransmissionLineIds(this)
}

fun EquipmentLibId.isEquipmentWithName() =
    this != EquipmentLibId.CONNECTIVITY
