package ru.nti.dtps.cimconverter.tocim.equipment.impl

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.Cim302Classes
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses
import ru.nti.dtps.cimconverter.tocim.equipment.AbstractEquipmentConversion
import ru.nti.dtps.cimconverter.tocim.equipment.EquipmentRelatedResources
import ru.nti.dtps.cimconverter.tocim.equipment.baseEquipmentConversion
import ru.nti.dtps.cimconverter.tocim.equipment.convertPorts
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.dto.scheme.getFieldDoubleValue
import ru.nti.dtps.dto.scheme.getFieldStringValue
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import java.util.*

object SynchronousGeneratorConverter : AbstractEquipmentConversion() {
    override fun convert(
        equipment: RawEquipmentNodeDto,
        scheme: RawSchemeDto,
        baseVoltage: RdfResource,
        baseVoltages: Map<VoltageLevelLibId, RdfResource>,
        linkIdToTnMap: Map<String, RdfResource>,
        linkIdToCnMap: Map<String, RdfResource>,
        voltageLevel: RdfResource,
        lines: Map<String, RdfResource>
    ): EquipmentRelatedResources {
        val resource = RdfResourceBuilder(equipment.id, CimClasses.SynchronousMachine)
            .baseEquipmentConversion(equipment, baseVoltage, voltageLevel)
            .addDataProperty(
                CimClasses.SynchronousMachine.ratedU,
                equipment.getFieldStringValue(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE)
            )
            .addDataProperty(
                CimClasses.SynchronousMachine.ratedS,
                equipment.getFieldStringValue(FieldLibId.RATED_APPARENT_POWER)
            )
            .addDataProperty(
                DtpsClasses.SynchronousMachine.isAvrEnabled,
                equipment.getFieldStringValue(FieldLibId.DRIVING_WINDING_AUTOMATIC_REGULATION) == "enabled"
            )
            .addDataProperty(
                DtpsClasses.SynchronousMachine.isArtfEnabled,
                equipment.getFieldStringValue(FieldLibId.SPEED_AUTOMATIC_REGULATION) == "enabled"
            )
            .addDataProperty(
                DtpsClasses.SynchronousMachine.isArapEnabled,
                equipment.getFieldStringValue(FieldLibId.POWER_AUTOMATIC_REGULATION) == "enabled"
            )
            .addDataProperty(
                DtpsClasses.SynchronousMachine.isAvrEnabledByDefalut,
                equipment.getFieldStringValue(FieldLibId.DEFAULT_DRIVING_WINDING_AUTOMATIC_REGULATION_STATE) == "enabled"
            )
            .addDataProperty(
                DtpsClasses.SynchronousMachine.isArtfEnabledByDefalut,
                equipment.getFieldStringValue(FieldLibId.DEFAULT_SPEED_AUTOMATIC_REGULATION_STATE) == "enabled"
            )
            .addDataProperty(
                DtpsClasses.SynchronousMachine.isArapEnabledByDefalut,
                equipment.getFieldStringValue(FieldLibId.DEFAULT_AUTOMATIC_SYSTEM_STATE) == "enabled"
            )
            .addDataProperty(
                DtpsClasses.SynchronousMachine.initialSpeed,
                equipment.getFieldStringValue(FieldLibId.INITIAL_SPEED)
            )
            .addDataProperty(
                DtpsClasses.SynchronousMachine.initialRotationAngle,
                equipment.getFieldStringValue(FieldLibId.INITIAL_ROTATION_ANGLE)
            )
            .addDataProperty(
                DtpsClasses.SynchronousMachine.initialEmf,
                equipment.getFieldStringValue(FieldLibId.INITIAL_ELECTROMOTIVE_FORCE)
            )
            .addDataProperty(
                CimClasses.SynchronousMachine.voltageRegulationRange,
                equipment.getFieldDoubleValue(FieldLibId.VOLTAGE_SETPOINT) * 100.0
            )
            .build()

        val rotatingMachineDynamics =
            RdfResourceBuilder(UUID.randomUUID().toString(), Cim302Classes.RotatingMachineDynamics)
                .addObjectProperty(
                    Cim302Classes.SynchronousMachineDynamics.SynchronousMachine,
                    resource
                )
                .addDataProperty(
                    Cim302Classes.RotatingMachineDynamics.inertia,
                    equipment.getFieldStringValue(FieldLibId.INERTIA_CONSTANT)
                )
                .addDataProperty(
                    Cim302Classes.RotatingMachineDynamics.statorLeakageReactance,
                    equipment.getFieldStringValue(FieldLibId.STATOR_LEAKAGE_REACTANCE)
                )
                .addDataProperty(
                    Cim302Classes.RotatingMachineDynamics.damping,
                    equipment.getFieldStringValue(FieldLibId.VISCOUS_FRICTION_COEFFICIENT)
                )
                .addDataProperty(
                    Cim302Classes.RotatingMachineDynamics.statorResistance,
                    equipment.getFieldStringValue(FieldLibId.STATOR_ACTIVE_RESISTANCE)
                )
                .build()

        val synchronousMachineEquivalentCircuit =
            RdfResourceBuilder(UUID.randomUUID().toString(), Cim302Classes.SynchronousMachineEquivalentCircuit)
                .addObjectProperty(
                    Cim302Classes.SynchronousMachineDynamics.SynchronousMachine,
                    resource
                )
                .addDataProperty(
                    Cim302Classes.SynchronousMachineEquivalentCircuit.xaq,
                    equipment.getFieldStringValue(FieldLibId.STATOR_ROTOR_MUTUAL_INDUCTANCE_Q_AXIS)
                )
                .addDataProperty(
                    Cim302Classes.SynchronousMachineEquivalentCircuit.xad,
                    equipment.getFieldStringValue(FieldLibId.STATOR_ROTOR_MUTUAL_INDUCTANCE_D_AXIS)
                )
                .addDataProperty(
                    Cim302Classes.SynchronousMachineEquivalentCircuit.xf1d,
                    equipment.getFieldStringValue(FieldLibId.FIRST_DAMPER_WINDING_LEAKAGE_REACTANCE_Q_AXIS)
                )
                .addDataProperty(
                    Cim302Classes.SynchronousMachineEquivalentCircuit.rfd,
                    equipment.getFieldStringValue(FieldLibId.DRIVING_WINDING_RESISTANCE)
                )
                .addDataProperty(
                    Cim302Classes.SynchronousMachineEquivalentCircuit.r1q,
                    equipment.getFieldStringValue(FieldLibId.FIRST_DAMPER_WINDING_RESISTANCE_Q_AXIS)
                )
                .addDataProperty(
                    Cim302Classes.SynchronousMachineEquivalentCircuit.r1d,
                    equipment.getFieldStringValue(FieldLibId.DAMPER_WINDING_RESISTANCE_D_AXIS)
                )
                .addDataProperty(
                    Cim302Classes.SynchronousMachineEquivalentCircuit.r2q,
                    equipment.getFieldStringValue(FieldLibId.SECOND_DAMPER_WINDING_RESISTANCE_Q_AXIS)
                )
//                .addDataProperty(
//                    Cim302Classes.SynchronousMachineEquivalentCircuit. ,
//                    equipment.getFieldStringValue(FieldLibId.DAMPER_WINDINGS_MUTUAL_INDUCTANCE_Q_AXIS)
//                )
                .addDataProperty(
                    Cim302Classes.SynchronousMachineEquivalentCircuit.x1d,
                    equipment.getFieldStringValue(FieldLibId.DAMPER_WINDING_LEAKAGE_REACTANCE_D_AXIS)
                )
                .addDataProperty(
                    Cim302Classes.SynchronousMachineEquivalentCircuit.x2q,
                    equipment.getFieldStringValue(FieldLibId.SECOND_DAMPER_WINDING_LEAKAGE_REACTANCE_Q_AXIS)
                )
                .addDataProperty(
                    Cim302Classes.SynchronousMachineEquivalentCircuit.xfd,
                    equipment.getFieldStringValue(FieldLibId.DAMPER_WINDING_DRIVING_WINDING_MUTUAL_INDUCTANCE_D_AXIS)
                )
                .build()

        val synchronousMachineTimeConstantReactance =
            RdfResourceBuilder(UUID.randomUUID().toString(), Cim302Classes.SynchronousMachineTimeConstantReactance)
                .addObjectProperty(
                    Cim302Classes.SynchronousMachineDynamics.SynchronousMachine,
                    resource
                )
                .addDataProperty(
                    Cim302Classes.SynchronousMachineTimeConstantReactance.xDirectSync,
                    equipment.getFieldStringValue(FieldLibId.DRIVING_WINDING_LEAKAGE_REACTANCE)
                )
                .build()

        val excitationSystemDynamics =
            RdfResourceBuilder(UUID.randomUUID().toString(), Cim302Classes.ExcREXS)
                .addObjectProperty(
                    Cim302Classes.ExcitationSystemDynamics.SynchronousMachineDynamics,
                    rotatingMachineDynamics
                )
                .addDataProperty(
                    Cim302Classes.ExcREXS.kvp,
                    equipment.getFieldStringValue(FieldLibId.CHANNEL_GAIN_BY_VOLTAGE_DEVIATION_COEFFICIENT)
                )
                .addDataProperty(
                    Cim302Classes.ExcREXS.tb1,
                    equipment.getFieldStringValue(FieldLibId.CHANNEL_INTEGRATION_TIME_CONSTANT_FOR_VOLTAGE_DEVIATION_COEFFICIENT)
                )
                .addDataProperty(
                    Cim302Classes.ExcREXS.kd,
                    equipment.getFieldStringValue(FieldLibId.STABILIZATION_CHANNEL_GAIN_BASED_ON_VOLTAGE_CHANGE_RATE)
                )
                .addDataProperty(
                    Cim302Classes.ExcREXS.ta,
                    equipment.getFieldStringValue(FieldLibId.STABILIZATION_CHANNEL_TIME_CONSTANT_FOR_VOLTAGE_CHANGE_RATE)
                )
//                .addDataProperty(
//                    Cim302Classes.ExcREXS.,
//                    equipment.getFieldStringValue(FieldLibId.STABILIZATION_CHANNEL_GAIN_BASED_ON_DRIVING_CURRENT_CHANGE_RATE)
//                )
                .addDataProperty(
                    Cim302Classes.ExcREXS.tf,
                    equipment.getFieldStringValue(FieldLibId.STABILIZATION_CHANNEL_TIME_CONSTANT_ON_DRIVING_CURRENT_CHANGE_RATE)
                )
                .addDataProperty(
                    Cim302Classes.ExcREXS.te,
                    equipment.getFieldStringValue(FieldLibId.DRIVING_SYSTEM_TIME_CONSTANT)
                )
                .addDataProperty(
                    Cim302Classes.ExcREXS.kf,
                    equipment.getFieldStringValue(FieldLibId.DRIVING_SYSTEM_GAIN)
                )
                .addDataProperty(
                    Cim302Classes.ExcREXS.vrmax,
                    equipment.getFieldStringValue(FieldLibId.DRIVING_SYSTEM_UPPER_LIMIT)
                )
                .addDataProperty(
                    Cim302Classes.ExcREXS.vrmin,
                    equipment.getFieldStringValue(FieldLibId.DRIVING_SYSTEM_LOWER_LIMIT)
                )
                .build()

        val turbineGovernorDynamics = RdfResourceBuilder(UUID.randomUUID().toString(), Cim302Classes.GovCT1)
            .addObjectProperty(
                Cim302Classes.TurbineGovernorDynamics.SynchronousMachineDynamics,
                rotatingMachineDynamics
            )
            .addDataProperty(
                Cim302Classes.GovCT1.tb,
                equipment.getFieldStringValue(FieldLibId.TURBINE_TIME_CONSTANT)
            )
            .addDataProperty(
                Cim302Classes.GovCT1.ldref,
                equipment.getFieldStringValue(FieldLibId.TURBINE_POWER_PU)
            )
            .addDataProperty(
                DtpsClasses.GovCT1.initialPosition,
                equipment.getFieldStringValue(FieldLibId.TURBINE_INITIAL_STATE)
            )
            .addDataProperty(
                Cim302Classes.GovCT1.ka,
                equipment.getFieldStringValue(FieldLibId.SPEED_RELAY_GAIN)
            )
            .addDataProperty(
                Cim302Classes.GovCT1.rdown,
                equipment.getFieldStringValue(FieldLibId.FLAP_STATE_LOWER_LIMIT)
            )
            .addDataProperty(
                Cim302Classes.GovCT1.rup,
                equipment.getFieldStringValue(FieldLibId.FLAP_STATE_UPPER_LIMIT)
            )
            .addDataProperty(
                DtpsClasses.GovCT1.frequencySetPoint,
                equipment.getFieldStringValue(FieldLibId.FREQUENCY_SETPOINT)
            )
            .addDataProperty(
                Cim302Classes.GovCT1.mwbase,
                equipment.getFieldDoubleValue(FieldLibId.POWER_SETPOINT) *
                    equipment.getFieldDoubleValue(FieldLibId.RATED_APPARENT_POWER)
            )
            .addDataProperty(
                Cim302Classes.GovCT1.kigov,
                equipment.getFieldStringValue(FieldLibId.INTEGRATION_OF_POWER_REGULATION_CONSTANT)
            )
            .build()

        return EquipmentRelatedResources(
            resource,
            convertPorts(equipment, linkIdToTnMap, linkIdToCnMap, resource),
            listOf(
                rotatingMachineDynamics,
                synchronousMachineEquivalentCircuit,
                synchronousMachineTimeConstantReactance,
                excitationSystemDynamics,
                turbineGovernorDynamics
            )
        )
    }
}
