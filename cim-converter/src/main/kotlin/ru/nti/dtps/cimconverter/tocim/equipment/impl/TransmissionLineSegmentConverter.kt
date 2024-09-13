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

object TransmissionLineSegmentConverter : AbstractEquipmentConversion() {
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
        val length = equipment.getFieldDoubleValue(FieldLibId.LENGTH)
        val resistancePosNegSeq = length * equipment.getFieldDoubleValue(
            FieldLibId.RESISTANCE_PER_LENGTH_POS_NEG_SEQ
        )
        val resistanceZeroSeq = length * equipment.getFieldDoubleValue(
            FieldLibId.RESISTANCE_PER_LENGTH_ZERO_SEQ
        )
        val reactancePosNegSeq = length * equipment.getFieldDoubleValue(
            FieldLibId.REACTANCE_PER_LENGTH_POS_NEG_SEQ
        )
        val reactanceZeroSeq = length * equipment.getFieldDoubleValue(
            FieldLibId.REACTANCE_PER_LENGTH_ZERO_SEQ
        )
        val susceptancePosNegSeq = length * equipment.getFieldDoubleValue(
            FieldLibId.SUSCEPTANCE_PER_LENGTH_POS_NEG_SEQ
        )
        val susceptanceZeroSeq = length * equipment.getFieldDoubleValue(
            FieldLibId.SUSCEPTANCE_PER_LENGTH_ZERO_SEQ
        )

        val line = equipment
            .getFieldStringValueOrNull(FieldLibId.TRANSMISSION_LINE)
            ?.let(lines::get)

        val resource = RdfResourceBuilder(equipment.id, CimClasses.ACLineSegment)
            .baseEquipmentConversion(equipment, baseVoltage, voltageLevel, line)
            .addDataProperty(
                CimClasses.Conductor.length,
                UnitsConverter.withDefaultCimMultiplier(
                    length,
                    CimClasses.Length
                )
            )
            .addDataProperty(
                CimClasses.ACLineSegment.r,
                UnitsConverter.withDefaultCimMultiplier(
                    resistancePosNegSeq,
                    CimClasses.Resistance
                )
            )
            .addDataProperty(
                CimClasses.ACLineSegment.r0,
                UnitsConverter.withDefaultCimMultiplier(
                    resistanceZeroSeq,
                    CimClasses.Resistance
                )
            )
            .addDataProperty(
                CimClasses.ACLineSegment.x,
                UnitsConverter.withDefaultCimMultiplier(
                    reactancePosNegSeq,
                    CimClasses.Resistance
                )
            )
            .addDataProperty(
                CimClasses.ACLineSegment.x0,
                UnitsConverter.withDefaultCimMultiplier(
                    reactanceZeroSeq,
                    CimClasses.Resistance
                )
            )
            .addDataProperty(
                CimClasses.ACLineSegment.bch,
                UnitsConverter.withDefaultCimMultiplier(
                    susceptancePosNegSeq,
                    CimClasses.Suseptance
                )
            )
            .addDataProperty(
                CimClasses.ACLineSegment.b0ch,
                UnitsConverter.withDefaultCimMultiplier(
                    susceptanceZeroSeq,
                    CimClasses.Suseptance
                )
            )
            .addDataProperty(
                DtpsClasses.ACLineSegment.ratedActivePower,
                equipment.getFieldDoubleValue(FieldLibId.RATED_ACTIVE_POWER)
            )
            .addDataProperty(
                DtpsClasses.ACLineSegment.useConcentratedParameters,
                equipment.getFieldStringValue(FieldLibId.USE_CONCENTRATED_PARAMETERS) == "enabled"
            )
            .build()

        return EquipmentRelatedResources(
            resource,
            convertPorts(equipment, linkIdToTnMap, linkIdToCnMap, resource)
        )
    }
}
