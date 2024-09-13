package ru.nti.dtps.cimconverter.tocim.equipment.impl.asynchronousmotor

import ru.nti.dtps.cimconverter.rdf.UnitsConverter
import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses
import ru.nti.dtps.cimconverter.tocim.equipment.AbstractEquipmentConversion
import ru.nti.dtps.cimconverter.tocim.equipment.EquipmentRelatedResources
import ru.nti.dtps.cimconverter.tocim.equipment.baseEquipmentConversion
import ru.nti.dtps.cimconverter.tocim.equipment.convertPorts
import ru.nti.dtps.dto.scheme.*
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId

object AsynchronousMotorConverter : AbstractEquipmentConversion() {

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
        val resource = RdfResourceBuilder(equipment.id, CimClasses.AsynchronousMachine)
            .baseEquipmentConversion(equipment, baseVoltage, voltageLevel)
            .addDataProperty(
                CimClasses.RotatingMachine.ratedU,
                UnitsConverter.withDefaultCimMultiplier(
                    equipment.getFieldValueWithMultiplier(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE),
                    CimClasses.Voltage
                )
            )
            .addDataProperty(
                CimClasses.AsynchronousMachine.ratedMechanicalPower,
                UnitsConverter.withDefaultCimMultiplier(
                    equipment.getFieldValueWithMultiplier(FieldLibId.RATED_ACTIVE_POWER),
                    CimClasses.ActivePower
                )
            )
            .addDataProperty(
                CimClasses.AsynchronousMachine.nominalFrequency,
                equipment.getFieldDoubleValue(FieldLibId.FREQUENCY)
            )
            .addDataProperty(
                CimClasses.AsynchronousMachine.reversible,
                equipment.getFieldStringValue(FieldLibId.REVERSE_ROTATION_ENABLED) == "enabled"
            )
            .addDataProperty(
                DtpsClasses.AsynchronousMachine.rotorLeakageReactance,
                equipment.getFieldStringValue(
                    FieldLibId.ROTOR_LEAKAGE_INDUCTANCE_REFERRED_TO_STATOR
                )
            )
            .build()

        return EquipmentRelatedResources(
            resource,
            convertPorts(equipment, linkIdToTnMap, linkIdToCnMap, resource),
            equipmentAdditionalResources = listOf(
                createAsynchronousMachineEquivalentCircuitResource(resource, equipment)
            )
        )
    }
}
