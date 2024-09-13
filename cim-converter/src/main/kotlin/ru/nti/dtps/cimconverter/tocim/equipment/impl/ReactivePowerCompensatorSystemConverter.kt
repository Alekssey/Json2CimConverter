package ru.nti.dtps.cimconverter.tocim.equipment.impl

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses
import ru.nti.dtps.cimconverter.tocim.equipment.AbstractEquipmentConversion
import ru.nti.dtps.cimconverter.tocim.equipment.EquipmentRelatedResources
import ru.nti.dtps.cimconverter.tocim.equipment.baseEquipmentConversion
import ru.nti.dtps.cimconverter.tocim.equipment.convertPorts
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.dto.scheme.getFieldDoubleValue
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId

object ReactivePowerCompensatorSystemConverter : AbstractEquipmentConversion() {
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
        val resource = RdfResourceBuilder(equipment.id, CimClasses.ShuntCompensator)
            .baseEquipmentConversion(equipment, baseVoltage, voltageLevel)
            .addDataProperty(CimClasses.ShuntCompensator.nomU, equipment.getFieldDoubleValue(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE))
            .addDataProperty(CimClasses.ShuntCompensator.aVRDelay, equipment.getFieldDoubleValue(FieldLibId.DRIVING_SYSTEM_TIME_CONSTANT))
            .addDataProperty(CimClasses.StaticVarCompensator.q, equipment.getFieldDoubleValue(FieldLibId.RATED_REACTIVE_POWER_OF_CAPACITOR_BANK))
            .addDataProperty(CimClasses.StaticVarCompensator.voltageSetPoint, equipment.getFieldDoubleValue(FieldLibId.VOLTAGE_SETPOINT))
            .addDataProperty(CimClasses.StaticVarCompensator.slope, equipment.getFieldDoubleValue(FieldLibId.GAIN))
            .addDataProperty(DtpsClasses.StaticVarCompensator.q, equipment.getFieldDoubleValue(FieldLibId.RATED_REACTIVE_POWER_OF_REACTOR))
            .addDataProperty(DtpsClasses.StaticVarCompensator.tU, equipment.getFieldDoubleValue(FieldLibId.INTEGRATION_TIME_CONSTANT))
            .build()

        return EquipmentRelatedResources(
            resource,
            convertPorts(equipment, linkIdToTnMap, linkIdToCnMap, resource)
        )
    }
}
