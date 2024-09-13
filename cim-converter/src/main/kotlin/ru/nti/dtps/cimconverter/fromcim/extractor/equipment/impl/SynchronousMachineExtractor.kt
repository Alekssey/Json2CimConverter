package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.extractor.EquipmentsExtractionResult
import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObject
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.RawEquipmentCreator
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.copyWithFields
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.machine.synchronous.*
import ru.nti.dtps.cimconverter.fromcim.extractor.link.PortInfo
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.basevoltage.BaseVoltage
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.voltagelevel.VoltageLevel
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractBooleanValueOrFalse
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractDoubleValueOrNull
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIdentifiedObjectId
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object SynchronousMachineExtractor : SealedEquipmentExtractor() {

    private val cimClass = CimClasses.SynchronousMachine
    private val equipmentLibId = EquipmentLibId.SYNCHRONOUS_GENERATOR

    override fun extract(
        repository: RdfRepository,
        substations: Map<String, RawSchemeDto.SubstationDto>,
        lines: Map<String, RawSchemeDto.TransmissionLineDto>,
        baseVoltages: Map<String, BaseVoltage>,
        voltageLevels: Map<String, VoltageLevel>,
        equipmentIdToPortsMap: Map<String, List<PortInfo>>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>,
        links: List<RawEquipmentLinkDto>,
        getEquipmentFrequencyOrDefault: (equipmentId: String) -> Double
    ) = EquipmentsExtractionResult(
        equipments = create(
            repository.selectAllVarsFromTriples(
                cimClass,
                CimClasses.IdentifiedObject.name,
                CimClasses.Equipment.EquipmentContainer,
                CimClasses.ConductingEquipment.BaseVoltage,
                CimClasses.SynchronousMachine.ratedU,
                CimClasses.SynchronousMachine.ratedS,
                DtpsClasses.SynchronousMachine.isAvrEnabled,
                DtpsClasses.SynchronousMachine.isArtfEnabled,
                DtpsClasses.SynchronousMachine.isArapEnabled,
                DtpsClasses.SynchronousMachine.isAvrEnabledByDefalut,
                DtpsClasses.SynchronousMachine.isArtfEnabledByDefalut,
                DtpsClasses.SynchronousMachine.isArapEnabledByDefalut,
                DtpsClasses.SynchronousMachine.initialSpeed,
                DtpsClasses.SynchronousMachine.initialRotationAngle,
                DtpsClasses.SynchronousMachine.initialEmf,
                CimClasses.SynchronousMachine.voltageRegulationRange
            ),
            repository,
            substations,
            lines,
            baseVoltages,
            voltageLevels,
            equipmentIdToPortsMap,
            objectIdToDiagramObjectMap,
            getEquipmentFrequencyOrDefault
        )
    )

    private fun create(
        queryResult: TupleQueryResult,
        repository: RdfRepository,
        substations: Map<String, RawSchemeDto.SubstationDto>,
        lines: Map<String, RawSchemeDto.TransmissionLineDto>,
        baseVoltages: Map<String, BaseVoltage>,
        voltageLevels: Map<String, VoltageLevel>,
        equipmentIdToPortsMap: Map<String, List<PortInfo>>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>,
        getEquipmentFrequencyOrDefault: (equipmentId: String) -> Double
    ): List<RawEquipmentNodeDto> {
        val synchronousMachineIdToRotatingMachineDynamicsMap =
            RotatingMachineDynamicsExtractor.extract(repository)
        val synchronousMachineIdToSynchronousMachineEquivalentCircuitMap =
            SynchronousMachineEquivalentCircuitExtractor.extract(repository)
        val synchronousMachineIdToSynchronousMachineTimeConstantReactanceMap =
            SynchronousMachineTimeConstantReactanceExtractor.extract(repository)

        val rotatingMachineDynamicsIdToExcitationSystemDynamicsMap =
            ExcitationSystemDynamicsExtractor.extract(repository)
        val rotatingMachineDynamicsIdToTurbineGovernorDynamicsMap =
            TurbineGovernorDynamicsExtractor.extract(repository)

        return queryResult.mapIndexed { index, bindingSet ->
            val id = bindingSet.extractIdentifiedObjectId()

            val apparentPower = bindingSet.extractDoubleValueOrNull(CimClasses.SynchronousMachine.ratedS) ?: 1.0

            val rotatingMachineDynamics =
                synchronousMachineIdToRotatingMachineDynamicsMap[id]
            val synchronousMachineEquivalentCircuit =
                synchronousMachineIdToSynchronousMachineEquivalentCircuitMap[id]
            val synchronousMachineTimeConstantReactance =
                synchronousMachineIdToSynchronousMachineTimeConstantReactanceMap[id]

            val excitationSystemDynamics =
                rotatingMachineDynamicsIdToExcitationSystemDynamicsMap[rotatingMachineDynamics?.id]
            val turbineGovernorDynamics =
                rotatingMachineDynamicsIdToTurbineGovernorDynamicsMap[rotatingMachineDynamics?.id]

            RawEquipmentCreator.equipmentWithBaseData(
                bindingSet,
                substations,
                lines,
                baseVoltages,
                voltageLevels,
                index + 1,
                equipmentLibId,
                equipmentIdToPortsMap,
                objectIdToDiagramObjectMap
            ).copyWithFields(
                FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to bindingSet.extractDoubleValueOrNull(
                    CimClasses.SynchronousMachine.ratedU
                ),
                FieldLibId.RATED_APPARENT_POWER to bindingSet.extractDoubleValueOrNull(
                    CimClasses.SynchronousMachine.ratedS
                ),
                FieldLibId.DRIVING_WINDING_AUTOMATIC_REGULATION to if (bindingSet.extractBooleanValueOrFalse(
                        DtpsClasses.SynchronousMachine.isAvrEnabled
                    )
                ) {
                    "enabled"
                } else {
                    "disabled"
                },
                FieldLibId.SPEED_AUTOMATIC_REGULATION to if (bindingSet.extractBooleanValueOrFalse(
                        DtpsClasses.SynchronousMachine.isArtfEnabled
                    )
                ) {
                    "enabled"
                } else {
                    "disabled"
                },
                FieldLibId.POWER_AUTOMATIC_REGULATION to if (bindingSet.extractBooleanValueOrFalse(
                        DtpsClasses.SynchronousMachine.isArapEnabled
                    )
                ) {
                    "enabled"
                } else {
                    "disabled"
                },
                FieldLibId.DEFAULT_DRIVING_WINDING_AUTOMATIC_REGULATION_STATE to if (bindingSet.extractBooleanValueOrFalse(
                        DtpsClasses.SynchronousMachine.isAvrEnabledByDefalut
                    )
                ) {
                    "enabled"
                } else {
                    "disabled"
                },
                FieldLibId.INITIAL_SPEED to bindingSet.extractDoubleValueOrNull(DtpsClasses.SynchronousMachine.initialSpeed),
                FieldLibId.INITIAL_ROTATION_ANGLE to bindingSet.extractDoubleValueOrNull(DtpsClasses.SynchronousMachine.initialRotationAngle),
                FieldLibId.INITIAL_ELECTROMOTIVE_FORCE to bindingSet.extractDoubleValueOrNull(DtpsClasses.SynchronousMachine.initialEmf),
                FieldLibId.VOLTAGE_SETPOINT to bindingSet
                    .extractDoubleValueOrNull(CimClasses.SynchronousMachine.voltageRegulationRange)
                    ?.let { it / 100.0 },
                FieldLibId.INERTIA_CONSTANT to rotatingMachineDynamics?.inertiaConstant,
                FieldLibId.STATOR_LEAKAGE_REACTANCE to rotatingMachineDynamics?.statorLeakageReactance,
                FieldLibId.VISCOUS_FRICTION_COEFFICIENT to rotatingMachineDynamics?.viscousFrictionCoefficient,
                FieldLibId.STATOR_ACTIVE_RESISTANCE to rotatingMachineDynamics?.statorActiveResistance,
                FieldLibId.STATOR_ROTOR_MUTUAL_INDUCTANCE_Q_AXIS to synchronousMachineEquivalentCircuit?.statorRotorMutualInductanceQAxis,
                FieldLibId.STATOR_ROTOR_MUTUAL_INDUCTANCE_Q_AXIS to synchronousMachineEquivalentCircuit?.statorRotorMutualInductanceQAxis,
                FieldLibId.FIRST_DAMPER_WINDING_LEAKAGE_REACTANCE_Q_AXIS to synchronousMachineEquivalentCircuit?.firstDamperWindingLeakageReactanceQAxis,
                FieldLibId.DRIVING_WINDING_RESISTANCE to synchronousMachineEquivalentCircuit?.drivingWindingResistance,
                FieldLibId.FIRST_DAMPER_WINDING_RESISTANCE_Q_AXIS to synchronousMachineEquivalentCircuit?.firstDamperWindingResistanceQAxis,
                FieldLibId.DAMPER_WINDING_RESISTANCE_D_AXIS to synchronousMachineEquivalentCircuit?.damperWindingResistanceDAxis,
                FieldLibId.SECOND_DAMPER_WINDING_RESISTANCE_Q_AXIS to synchronousMachineEquivalentCircuit?.secondDamperWindingResistanceQAxis,
                FieldLibId.DAMPER_WINDING_LEAKAGE_REACTANCE_D_AXIS to synchronousMachineEquivalentCircuit?.damperWindingLeakageReactanceDAxis,
                FieldLibId.SECOND_DAMPER_WINDING_LEAKAGE_REACTANCE_Q_AXIS to synchronousMachineEquivalentCircuit?.secondDamperWindingLeakageReactanceQAxis,
                FieldLibId.DAMPER_WINDING_DRIVING_WINDING_MUTUAL_INDUCTANCE_D_AXIS to synchronousMachineEquivalentCircuit?.damperWindingDrivingMutualIndactanceDAxis,
                FieldLibId.DRIVING_WINDING_LEAKAGE_REACTANCE to synchronousMachineTimeConstantReactance?.drivingWindingLeakageReactance,
                FieldLibId.CHANNEL_GAIN_BY_VOLTAGE_DEVIATION_COEFFICIENT to excitationSystemDynamics?.channelGainByVoltageDeviationCoefficient,
                FieldLibId.CHANNEL_INTEGRATION_TIME_CONSTANT_FOR_VOLTAGE_DEVIATION_COEFFICIENT to excitationSystemDynamics?.channelIntegrationTimeConstantForVoltageDeviationCoefficient,
                FieldLibId.STABILIZATION_CHANNEL_GAIN_BASED_ON_VOLTAGE_CHANGE_RATE to excitationSystemDynamics?.stabilizationChannelGainBasedOnVoltageChangeRate,
                FieldLibId.STABILIZATION_CHANNEL_TIME_CONSTANT_FOR_VOLTAGE_CHANGE_RATE to excitationSystemDynamics?.stabilizationChannelTimeConstantForVoltageChangeRate,
                FieldLibId.STABILIZATION_CHANNEL_TIME_CONSTANT_ON_DRIVING_CURRENT_CHANGE_RATE to excitationSystemDynamics?.stabilizationChannelTimeConstantOnDrivingCurrentChangeRate,
                FieldLibId.DRIVING_SYSTEM_TIME_CONSTANT to excitationSystemDynamics?.drivingSystemTimeConstant,
                FieldLibId.DRIVING_SYSTEM_GAIN to excitationSystemDynamics?.drivingSystemGain,
                FieldLibId.DRIVING_SYSTEM_UPPER_LIMIT to excitationSystemDynamics?.drivingSystemUpperLimit,
                FieldLibId.DRIVING_SYSTEM_LOWER_LIMIT to excitationSystemDynamics?.drivingSystemLowerLimit,
                FieldLibId.TURBINE_TIME_CONSTANT to turbineGovernorDynamics?.turbineTimeConstant,
                FieldLibId.TURBINE_POWER_PU to turbineGovernorDynamics?.turbinePowerPu,
                FieldLibId.TURBINE_INITIAL_STATE to turbineGovernorDynamics?.turbineInitialState,
                FieldLibId.SPEED_RELAY_GAIN to turbineGovernorDynamics?.speedRelayGain,
                FieldLibId.FLAP_STATE_LOWER_LIMIT to turbineGovernorDynamics?.flapStateLowerLimit,
                FieldLibId.FLAP_STATE_UPPER_LIMIT to turbineGovernorDynamics?.flapStateUpperLimit,
                FieldLibId.FREQUENCY_SETPOINT to turbineGovernorDynamics?.frequencySetPoint,
                FieldLibId.POWER_SETPOINT to turbineGovernorDynamics?.powerSetPoint?.let { it / apparentPower },
                FieldLibId.INTEGRATION_OF_POWER_REGULATION_CONSTANT to turbineGovernorDynamics?.integrationOfPowerRegulationConstant
            )
        }
    }
}
