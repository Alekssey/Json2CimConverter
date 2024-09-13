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
import ru.nti.dtps.dto.scheme.getFieldStringValue
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId

object CurrentTransformerConverter : AbstractEquipmentConversion() {
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
        val resource = RdfResourceBuilder(equipment.id, CimClasses.CurrentTransformer)
            .baseEquipmentConversion(equipment, baseVoltage, voltageLevel)
            .addDataProperty(
                DtpsClasses.CurrentTransformer.primaryWindingTurnAmount,
                equipment.getFieldStringValue(FieldLibId.PRIMARY_WINDING_TURN_AMOUNT)
            )
            .addDataProperty(
                DtpsClasses.CurrentTransformer.secondaryWindingTurnAmount,
                equipment.getFieldStringValue(FieldLibId.SECONDARY_WINDING_TURN_AMOUNT)
            )
            .addDataProperty(
                DtpsClasses.CurrentTransformer.coreCrossSection,
                equipment.getFieldStringValue(FieldLibId.CORE_CROSS_SECTION)
            )
            .addDataProperty(
                DtpsClasses.CurrentTransformer.magneticPathAverageLength,
                equipment.getFieldStringValue(FieldLibId.MAGNETIC_PATH_AVERAGE_LENGTH)
            )
            .addDataProperty(
                DtpsClasses.CurrentTransformer.secondaryWindingResistance,
                equipment.getFieldStringValue(FieldLibId.SECONDARY_WINDING_RESISTANCE)
            )
            .addDataProperty(
                DtpsClasses.CurrentTransformer.secondaryWindingInductance,
                equipment.getFieldStringValue(FieldLibId.SECONDARY_WINDING_INDUCTANCE)
            )
            .addDataProperty(
                DtpsClasses.CurrentTransformer.secondaryWindingLoadResistance,
                equipment.getFieldStringValue(FieldLibId.SECONDARY_WINDING_LOAD_RESISTANCE)
            )
            .addDataProperty(
                DtpsClasses.CurrentTransformer.secondaryWindingLoadInductance,
                equipment.getFieldStringValue(FieldLibId.SECONDARY_WINDING_LOAD_INDUCTANCE)
            )
            .addDataProperty(
                DtpsClasses.CurrentTransformer.magnetizationCurveFirstCoefficient,
                equipment.getFieldStringValue(FieldLibId.FIRST_COEFFICIENT_OF_MAGNETIZATION_CURVE)
            )
            .addDataProperty(
                DtpsClasses.CurrentTransformer.magnetizationCurveSecondCoefficient,
                equipment.getFieldStringValue(FieldLibId.SECOND_COEFFICIENT_OF_MAGNETIZATION_CURVE)
            )
            .addDataProperty(
                DtpsClasses.CurrentTransformer.magnetizationCurveThirdCoefficient,
                equipment.getFieldStringValue(FieldLibId.THIRD_COEFFICIENT_OF_MAGNETIZATION_CURVE)
            )
            .addEnumProperty(
                DtpsClasses.CurrentTransformer.modelType,
                when (equipment.getFieldStringValue(FieldLibId.CURRENT_TRANSFORMER_MODEL)) {
                    "ideal" -> DtpsClasses.CurrentTransformerModelType.ideal
                    else -> DtpsClasses.CurrentTransformerModelType.real
                }
            )
            .build()

        return EquipmentRelatedResources(
            resource,
            convertPorts(equipment, linkIdToTnMap, linkIdToCnMap, resource)
        )
    }
}
