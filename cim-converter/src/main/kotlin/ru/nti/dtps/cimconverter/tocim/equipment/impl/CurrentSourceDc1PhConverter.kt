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

object CurrentSourceDc1PhConverter : AbstractEquipmentConversion() {
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
        val resource = RdfResourceBuilder(equipment.id, DtpsClasses.DcCurrentSourceByPhase)
            .baseEquipmentConversion(equipment, baseVoltage, voltageLevel)
            .addDataProperty(
                DtpsClasses.DcCurrentSourceByPhase.conductivity,
                equipment.getFieldDoubleValue(FieldLibId.CONDUCTIVITY)
            )
            .addDataProperty(
                DtpsClasses.DcCurrentSourceByPhase.current,
                equipment.getFieldDoubleValue(FieldLibId.CURRENT)
            )
            .build()

        return EquipmentRelatedResources(
            resource,
            convertPorts(equipment, linkIdToTnMap, linkIdToCnMap, resource) { CimClasses.PhaseCode.A }
        )
    }
}
