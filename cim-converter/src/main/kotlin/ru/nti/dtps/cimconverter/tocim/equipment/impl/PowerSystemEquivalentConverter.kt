package ru.nti.dtps.cimconverter.tocim.equipment.impl

import ru.nti.dtps.cimconverter.degreeToRad
import ru.nti.dtps.cimconverter.rdf.UnitsConverter
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
import kotlin.math.cos
import kotlin.math.sin

object PowerSystemEquivalentConverter : AbstractEquipmentConversion() {
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
        val (resistancePosNegSeq, reactancePosNegSeq) = calculateResistanceAndReactance(
            equipment.getFieldDoubleValue(FieldLibId.IMPEDANCE_POS_NEG_SEQ),
            equipment.getFieldDoubleValue(FieldLibId.ANGLE_OF_IMPEDANCE_POS_NEG_SEQ)
        )

        val (resistanceZeroSeq, reactanceZeroSeq) = calculateResistanceAndReactance(
            equipment.getFieldDoubleValue(FieldLibId.IMPEDANCE_ZERO_SEQ),
            equipment.getFieldDoubleValue(FieldLibId.ANGLE_OF_IMPEDANCE_ZERO_SEQ)
        )

        val resource = RdfResourceBuilder(equipment.id, CimClasses.EquivalentInjection)
            .baseEquipmentConversion(equipment, baseVoltage, voltageLevel)
            .addDataProperty(
                CimClasses.EquivalentInjection.r,
                UnitsConverter.withDefaultCimMultiplier(
                    resistancePosNegSeq,
                    CimClasses.Resistance
                )
            )
            .addDataProperty(
                CimClasses.EquivalentInjection.r2,
                UnitsConverter.withDefaultCimMultiplier(
                    resistancePosNegSeq,
                    CimClasses.Resistance
                )
            )
            .addDataProperty(
                CimClasses.EquivalentInjection.x,
                UnitsConverter.withDefaultCimMultiplier(
                    reactancePosNegSeq,
                    CimClasses.Resistance
                )
            )
            .addDataProperty(
                CimClasses.EquivalentInjection.x2,
                UnitsConverter.withDefaultCimMultiplier(
                    reactancePosNegSeq,
                    CimClasses.Resistance
                )
            )
            .addDataProperty(
                CimClasses.EquivalentInjection.r0,
                UnitsConverter.withDefaultCimMultiplier(
                    resistanceZeroSeq,
                    CimClasses.Resistance
                )
            )
            .addDataProperty(
                CimClasses.EquivalentInjection.x0,
                UnitsConverter.withDefaultCimMultiplier(
                    reactanceZeroSeq,
                    CimClasses.Resistance
                )
            )
            .addDataProperty(
                DtpsClasses.EquivalentInjection.emfTimeConstant,
                equipment.getFieldDoubleValue(FieldLibId.EMF_TIME_CONSTANT)
            )
            .build()

        return EquipmentRelatedResources(
            resource,
            convertPorts(equipment, linkIdToTnMap, linkIdToCnMap, resource)
        )
    }

    private fun calculateResistanceAndReactance(
        impedance: Double,
        angleDegree: Double
    ): Pair<Double, Double> {
        val angleRad = degreeToRad(angleDegree)
        return impedance * cos(angleRad) to impedance * sin(angleRad)
    }
}
