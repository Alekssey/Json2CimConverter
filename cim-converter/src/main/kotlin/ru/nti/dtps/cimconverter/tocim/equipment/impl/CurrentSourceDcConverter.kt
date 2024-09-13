package ru.nti.dtps.cimconverter.tocim.equipment.impl

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
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

object CurrentSourceDcConverter : AbstractEquipmentConversion() {
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
        val resource = RdfResourceBuilder(equipment.id, DtpsClasses.DcCurrentSource)
            .baseEquipmentConversion(equipment, baseVoltage, voltageLevel)
            .addDataProperty(
                DtpsClasses.DcCurrentSource.currentPhaseA,
                equipment.getFieldDoubleValue(FieldLibId.CONDUCTIVITY_PHASE_A)
            )
            .addDataProperty(
                DtpsClasses.DcCurrentSource.currentPhaseB,
                equipment.getFieldDoubleValue(FieldLibId.CONDUCTIVITY_PHASE_B)
            )
            .addDataProperty(
                DtpsClasses.DcCurrentSource.currentPhaseC,
                equipment.getFieldDoubleValue(FieldLibId.CONDUCTIVITY_PHASE_C)
            )
            .addDataProperty(
                DtpsClasses.DcCurrentSource.currentPhaseA,
                equipment.getFieldDoubleValue(FieldLibId.CURRENT_PHASE_A)
            )
            .addDataProperty(
                DtpsClasses.DcCurrentSource.currentPhaseB,
                equipment.getFieldDoubleValue(FieldLibId.CURRENT_PHASE_B)
            )
            .addDataProperty(
                DtpsClasses.DcCurrentSource.currentPhaseC,
                equipment.getFieldDoubleValue(FieldLibId.CURRENT_PHASE_C)
            )
            .build()

        return EquipmentRelatedResources(
            resource,
            convertPorts(equipment, linkIdToTnMap, linkIdToCnMap, resource)
        )
    }
}
