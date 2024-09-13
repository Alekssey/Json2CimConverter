package ru.nti.dtps.cimconverter.tocim.equipment

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.tocim.equipment.auxiliary.AuxEquipmentConverter
import ru.nti.dtps.cimconverter.tocim.equipment.impl.*
import ru.nti.dtps.cimconverter.tocim.equipment.impl.asynchronousmotor.AsynchronousMotorConverter
import ru.nti.dtps.cimconverter.tocim.equipment.impl.transformer.auto.ThreeWindingAutoTransformerConverter
import ru.nti.dtps.cimconverter.tocim.equipment.impl.transformer.auto.TwoWindingAutoTransformerConverter
import ru.nti.dtps.cimconverter.tocim.equipment.impl.transformer.ordinary.ThreeWindingPowerTransformerConverter
import ru.nti.dtps.cimconverter.tocim.equipment.impl.transformer.ordinary.TwoWindingPowerTransformerConverter
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId.*
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId

object EquipmentConverter {

    fun convert(
        scheme: RawSchemeDto,
        lines: Map<String, RdfResource>,
        baseVoltages: Map<VoltageLevelLibId, RdfResource>,
        voltageLevelIdToVoltageLevelResourceMap: Map<String, RdfResource>,
        voltageLevelIdToEquipmentsMap: Map<String, List<RawEquipmentNodeDto>>,
        linkIdToTnMap: Map<String, RdfResource>,
        linkIdToCnMap: Map<String, RdfResource>
    ): EquipmentsRelatedResources {
        val equipmentIdToResourceMap = mutableMapOf<String, RdfResource>()
        val portIdToTerminalResourceMap = mutableMapOf<String, RdfResource>()
        val equipmentAdditionalResources = mutableListOf<RdfResource>()

        voltageLevelIdToEquipmentsMap.forEach { (voltageLevelId, equipments) ->
            equipments.filter {
                it.libEquipmentId != CONNECTIVITY &&
                    !AuxEquipmentConverter.auxEquipmentLibIds.contains(it.libEquipmentId)
            }.forEach { equipment ->
                val voltageLevel = voltageLevelIdToVoltageLevelResourceMap[voltageLevelId]!!

                val converter: AbstractEquipmentConversion = when (equipment.libEquipmentId) {
                    BUS -> BusConverter
                    TRANSMISSION_LINE_SEGMENT -> TransmissionLineSegmentConverter
                    TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT -> TransmissionLineSegmentDoubleCircuitConverter
                    POWER_SYSTEM_EQUIVALENT -> PowerSystemEquivalentConverter
                    CIRCUIT_BREAKER, CIRCUIT_BREAKER_1PH -> CircuitBreakerConverter
                    TWO_WINDING_POWER_TRANSFORMER -> TwoWindingPowerTransformerConverter
                    TWO_WINDING_AUTO_TRANSFORMER -> TwoWindingAutoTransformerConverter
                    THREE_WINDING_POWER_TRANSFORMER -> ThreeWindingPowerTransformerConverter
                    THREE_WINDING_AUTO_TRANSFORMER -> ThreeWindingAutoTransformerConverter
                    LOAD -> LoadConverter
                    ASYNCHRONOUS_MOTOR -> AsynchronousMotorConverter
                    GROUNDING, GROUNDING_1PH -> GroundingConverter
                    CURRENT_TRANSFORMER -> CurrentTransformerConverter
                    RESISTANCE, RESISTANCE_1PH -> ResistanceConverter
                    SYNCHRONOUS_GENERATOR -> SynchronousGeneratorConverter
                    REACTIVE_POWER_COMPENSATION_SYSTEM -> ReactivePowerCompensatorSystemConverter
                    INDUCTANCE, INDUCTANCE_1PH -> InductanceConverter
                    CAPACITANCE, CAPACITANCE_1PH -> CapacitanceConverter
                    THREE_PHASE_CONNECTOR -> ThreePhaseConnectorConverter
                    DISCONNECTOR, DISCONNECTOR_1PH -> DisconnectorConverter
                    GROUND_DISCONNECTOR, GROUND_DISCONNECTOR_1PH -> GroundDisconnectorConverter
                    ELECTRICITY_STORAGE_SYSTEM -> ElectricityStorageSystemConverter
                    CURRENT_SOURCE_DC -> CurrentSourceDcConverter
                    CURRENT_SOURCE_DC_1PH -> CurrentSourceDc1PhConverter
                    SOURCE_OF_ELECTROMOTIVE_FORCE_DC -> SourceOfElectromotiveForceDcConverter
                    SOURCE_OF_ELECTROMOTIVE_FORCE_DC_1PH -> SourceOfElectromotiveForceDc1PhConverter
                    VOLTAGE_TRANSFORMER -> throw IllegalArgumentException(
                        "Voltage transformer conversion is not expected here"
                    )

                    SHORT_CIRCUIT, SHORT_CIRCUIT_1PH -> throw IllegalArgumentException(
                        "Short circuit conversion is not expected here"
                    )

                    CONNECTIVITY -> throw IllegalArgumentException(
                        "Connectivity conversion is not expected here"
                    )

                    UNRECOGNIZED -> throw IllegalArgumentException(
                        "Unrecognized equipment lib id"
                    )

                    INDICATOR, BUTTON, MEASUREMENT, VALUE -> throw IllegalArgumentException(
                        "Dashboard specific nodes is not expected here"
                    )
                }

                val equipmentRelatedResources = converter.convert(
                    equipment = equipment,
                    scheme = scheme,
                    baseVoltage = baseVoltages[equipment.voltageLevelId]!!,
                    baseVoltages = baseVoltages,
                    linkIdToTnMap = linkIdToTnMap,
                    linkIdToCnMap = linkIdToCnMap,
                    voltageLevel = voltageLevel,
                    lines = lines
                )

                equipmentIdToResourceMap[equipment.id] = equipmentRelatedResources.equipmentResource
                portIdToTerminalResourceMap += equipmentRelatedResources.portIdToTerminalResourceMap
                equipmentAdditionalResources += equipmentRelatedResources.equipmentAdditionalResources
            }
        }

        val auxEquipmentIdToResources = AuxEquipmentConverter.convert(
            scheme,
            equipmentIdToResourceMap,
            portIdToTerminalResourceMap
        )

        return EquipmentsRelatedResources(
            equipmentIdToResourceMap,
            portIdToTerminalResourceMap,
            equipmentAdditionalResources,
            auxEquipmentIdToResources
        )
    }
}
