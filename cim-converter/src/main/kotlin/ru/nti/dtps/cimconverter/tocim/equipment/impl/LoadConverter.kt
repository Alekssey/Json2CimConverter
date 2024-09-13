package ru.nti.dtps.cimconverter.tocim.equipment.impl

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
import java.util.UUID

object LoadConverter : AbstractEquipmentConversion() {
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
        val loadResponse = RdfResourceBuilder(UUID.randomUUID().toString(), CimClasses.LoadResponseCharacteristic)
            .addDataProperty(
                CimClasses.LoadResponseCharacteristic.pVoltageExponent,
                equipment.getFieldDoubleValue(FieldLibId.ACTIVE_POWER_TO_FREQUENCY_COEFFICIENT)
            )
            .addDataProperty(
                CimClasses.LoadResponseCharacteristic.qVoltageExponent,
                equipment.getFieldDoubleValue(FieldLibId.ACTIVE_POWER_TO_VOLTAGE_COEFFICIENT)
            )
            .addDataProperty(
                CimClasses.LoadResponseCharacteristic.qConstantPower,
                equipment.getFieldDoubleValue(FieldLibId.ACTIVE_POWER_TO_REACTIVE_POWER_COEFFICIENT)
            )
            .build()

        val resource = RdfResourceBuilder(equipment.id, CimClasses.EnergyConsumer)
            .baseEquipmentConversion(equipment, baseVoltage, voltageLevel)
            .addDataProperty(
                CimClasses.EnergyConsumer.grounded,
                "enabled" == equipment.getFieldStringValue(FieldLibId.GROUNDED)
            )
            .addDataProperty(
                CimClasses.EnergyConsumer.pfixed,
                UnitsConverter.withDefaultCimMultiplier(
                    equipment.getFieldValueWithMultiplier(FieldLibId.ACTIVE_POWER),
                    CimClasses.ActivePower
                )
            )
            .addDataProperty(
                CimClasses.EnergyConsumer.p,
                UnitsConverter.withDefaultCimMultiplier(
                    equipment.getFieldValueWithMultiplier(FieldLibId.ACTIVE_POWER),
                    CimClasses.ActivePower
                )
            )
            .addDataProperty(
                CimClasses.EnergyConsumer.qfixed,
                UnitsConverter.withDefaultCimMultiplier(
                    equipment.getFieldValueWithMultiplier(FieldLibId.REACTIVE_POWER),
                    CimClasses.ReactivePower
                )
            )
            .addDataProperty(
                CimClasses.EnergyConsumer.q,
                UnitsConverter.withDefaultCimMultiplier(
                    equipment.getFieldValueWithMultiplier(FieldLibId.REACTIVE_POWER),
                    CimClasses.ReactivePower
                )
            )
            .addObjectProperty(
                CimClasses.EnergyConsumer.LoadResponse,
                loadResponse
            )
            .addDataProperty(
                DtpsClasses.EnergyConsumer.ratedVoltage,
                equipment.getFieldDoubleValue(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE)
            )
            .build()

        return EquipmentRelatedResources(
            resource,
            convertPorts(equipment, linkIdToTnMap, linkIdToCnMap, resource),
            listOf(loadResponse)
        )
    }
}
