package ru.nti.dtps.cimconverter.tocim.equipment.impl.transformer

import ru.nti.dtps.cimconverter.ext.isThreeWindingPowerTransformer
import ru.nti.dtps.cimconverter.ext.isTwoWindingPowerTransformer
import ru.nti.dtps.cimconverter.rdf.UnitsConverter
import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceId
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.CimEnum
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses
import ru.nti.dtps.dto.scheme.*
import ru.nti.dtps.equipment.meta.info.dataclass.voltagelevel.VoltageLevelLib
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.equipment.meta.info.manager.winding.WindingType
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import java.util.*
import kotlin.math.abs

fun createFirstWindingPowerTransformerEndResource(
    equipment: RawEquipmentNodeDto,
    powerTransformer: RdfResource,
    terminals: List<RdfResource>,
    baseVoltages: Map<VoltageLevelLibId, RdfResource>,
    windingType: WindingType,
    ratioTapChangerId: RdfResourceId?
): RdfResource = createPowerTransformerEndResource(
    equipment,
    equipment.getFieldDoubleValue(FieldLibId.FIRST_WINDING_RATED_VOLTAGE),
    powerTransformer,
    terminals,
    baseVoltages,
    windingType,
    1,
    "First",
    ratioTapChangerId = if (
        ratioTapChangerId != null &&
        equipment.getFieldStringValue(FieldLibId.TAP_CHANGER_INSTALLATION_WINDING) == "onFirstWinding"
    ) {
        ratioTapChangerId
    } else {
        null
    }
)

fun createSecondWindingPowerTransformerEndResource(
    equipment: RawEquipmentNodeDto,
    powerTransformer: RdfResource,
    terminals: List<RdfResource>,
    baseVoltages: Map<VoltageLevelLibId, RdfResource>,
    windingType: WindingType,
    ratioTapChangerId: RdfResourceId?
): RdfResource = createPowerTransformerEndResource(
    equipment,
    equipment.getFieldDoubleValue(FieldLibId.SECOND_WINDING_RATED_VOLTAGE),
    powerTransformer,
    terminals,
    baseVoltages,
    windingType,
    2,
    "Second",
    ratioTapChangerId = if (
        ratioTapChangerId != null &&
        equipment.getFieldStringValue(FieldLibId.TAP_CHANGER_INSTALLATION_WINDING) == "onSecondWinding"
    ) {
        ratioTapChangerId
    } else {
        null
    }
)

fun createThirdWindingPowerTransformerEndResource(
    equipment: RawEquipmentNodeDto,
    powerTransformer: RdfResource,
    terminals: List<RdfResource>,
    baseVoltages: Map<VoltageLevelLibId, RdfResource>,
    windingType: WindingType,
    ratioTapChangerId: RdfResourceId?
): RdfResource = createPowerTransformerEndResource(
    equipment,
    equipment.getFieldDoubleValue(FieldLibId.THIRD_WINDING_RATED_VOLTAGE),
    powerTransformer,
    terminals,
    baseVoltages,
    windingType,
    3,
    "Third",
    ratioTapChangerId = if (
        ratioTapChangerId != null &&
        equipment.getFieldStringValue(FieldLibId.TAP_CHANGER_INSTALLATION_WINDING) == "onThirdWinding"
    ) {
        ratioTapChangerId
    } else {
        null
    }
)

private fun createPowerTransformerEndResource(
    equipment: RawEquipmentNodeDto,
    ratedVoltageInKilovolts: Double,
    powerTransformer: RdfResource,
    terminals: List<RdfResource>,
    baseVoltages: Map<VoltageLevelLibId, RdfResource>,
    windingType: WindingType,
    endNumber: Int,
    namePrefix: String,
    ratioTapChangerId: RdfResourceId?
): RdfResource {
    val ratedApparentPowerInVoltsAmpere = equipment.getFieldValueWithMultiplier(FieldLibId.RATED_APPARENT_POWER)
    val squaredFirstWindingRatedVoltageInVolts = (
        equipment.getFieldValueWithMultiplier(
            FieldLibId.FIRST_WINDING_RATED_VOLTAGE
        ) * 1000
        ).let { it * it }
    val ratedVoltageInVolts = ratedVoltageInKilovolts * 1000

    return RdfResourceBuilder(UUID.randomUUID().toString(), CimClasses.PowerTransformerEnd)
        .addDataProperty(CimClasses.IdentifiedObject.name, "$namePrefix winding of ${equipment.name()}")
        .addDataProperty(CimClasses.TransformerEnd.endNumber, endNumber.toString())
        .addDataProperty(
            CimClasses.PowerTransformerEnd.phaseAngleClock,
            when (windingType) {
                WindingType.D1 -> 1
                WindingType.D11 -> 11
                else -> 0
            }
        )
        .addDataProperty(
            CimClasses.TransformerEnd.grounded,
            when (windingType) {
                WindingType.Yg -> "true"
                else -> "false"
            }
        )
        .addEnumProperty(
            CimClasses.PowerTransformerEnd.connectionKind,
            convertConnectionKind(windingType)
        )
        .addDataProperty(
            CimClasses.PowerTransformerEnd.ratedU,
            UnitsConverter.withDefaultCimMultiplier(
                ratedVoltageInVolts,
                CimClasses.Voltage
            )
        )
        .addDataProperty(
            CimClasses.PowerTransformerEnd.ratedS,
            UnitsConverter.withDefaultCimMultiplier(
                ratedApparentPowerInVoltsAmpere,
                CimClasses.ApparentPower
            )
        )
        .addDataProperty(
            CimClasses.PowerTransformerEnd.g,
            UnitsConverter.withDefaultCimMultiplier(
                equipment.getFieldDoubleValue(FieldLibId.IDLING_ACTIVE_POWER) * 1e3 /
                    (ratedVoltageInVolts * ratedVoltageInVolts),
                CimClasses.Conductance
            )
        )
        .addDataProperty(
            CimClasses.PowerTransformerEnd.b,
            UnitsConverter.withDefaultCimMultiplier(
                equipment.getFieldDoubleValue(FieldLibId.IDLING_CURRENT) * ratedApparentPowerInVoltsAmpere /
                    (100 * ratedVoltageInVolts * ratedVoltageInVolts),
                CimClasses.Suseptance
            )
        )
        .addDataProperty(
            DtpsClasses.PowerTransformerEnd.doesSaturationExist,
            equipment.getFieldStringValue(FieldLibId.SATURATION_EXIST).let { saturationExists ->
                when (endNumber) {
                    1 -> saturationExists == "onFirstWinding"
                    2 -> saturationExists == "onSecondWinding"
                    3 -> saturationExists == "onThirdWinding"
                    else -> throw RuntimeException("Unexpected endNumber $endNumber")
                }
            }
        )
        .let { powerTransformerEndResource ->
            when {
                equipment.isTwoWindingPowerTransformer() -> powerTransformerEndResource.addDataProperty(
                    CimClasses.PowerTransformerEnd.x,
                    UnitsConverter.withDefaultCimMultiplier(
                        equipment.getFieldDoubleValue(FieldLibId.SHORT_CIRCUIT_VOLTAGE) *
                            ratedVoltageInVolts * ratedVoltageInVolts / (100 * ratedApparentPowerInVoltsAmpere),
                        CimClasses.Reactance
                    )
                )

                equipment.isThreeWindingPowerTransformer() -> when (endNumber) {
                    1 -> powerTransformerEndResource.addDataProperty(
                        CimClasses.PowerTransformerEnd.x,
                        UnitsConverter.withDefaultCimMultiplier(
                            0.5 * (
                                equipment.getFieldDoubleValue(FieldLibId.FIRST_SECOND_WINDING_SHORT_CIRCUIT_VOLTAGE) +
                                    equipment.getFieldDoubleValue(FieldLibId.FIRST_THIRD_WINDING_SHORT_CIRCUIT_VOLTAGE) -
                                    equipment.getFieldDoubleValue(FieldLibId.SECOND_THIRD_WINDING_SHORT_CIRCUIT_VOLTAGE)
                                ) *
                                squaredFirstWindingRatedVoltageInVolts / (100 * ratedApparentPowerInVoltsAmpere),
                            CimClasses.Reactance
                        )
                    )

                    2 -> powerTransformerEndResource.addDataProperty(
                        CimClasses.PowerTransformerEnd.x,
                        UnitsConverter.withDefaultCimMultiplier(
                            0.5 * (
                                equipment.getFieldDoubleValue(FieldLibId.FIRST_SECOND_WINDING_SHORT_CIRCUIT_VOLTAGE) -
                                    equipment.getFieldDoubleValue(FieldLibId.FIRST_THIRD_WINDING_SHORT_CIRCUIT_VOLTAGE) +
                                    equipment.getFieldDoubleValue(FieldLibId.SECOND_THIRD_WINDING_SHORT_CIRCUIT_VOLTAGE)
                                ) *
                                squaredFirstWindingRatedVoltageInVolts / (100 * ratedApparentPowerInVoltsAmpere),
                            CimClasses.Reactance
                        )
                    )

                    3 -> powerTransformerEndResource.addDataProperty(
                        CimClasses.PowerTransformerEnd.x,
                        UnitsConverter.withDefaultCimMultiplier(
                            0.5 * (
                                -equipment.getFieldDoubleValue(FieldLibId.FIRST_SECOND_WINDING_SHORT_CIRCUIT_VOLTAGE) +
                                    equipment.getFieldDoubleValue(FieldLibId.FIRST_THIRD_WINDING_SHORT_CIRCUIT_VOLTAGE) +
                                    equipment.getFieldDoubleValue(FieldLibId.SECOND_THIRD_WINDING_SHORT_CIRCUIT_VOLTAGE)
                                ) *
                                squaredFirstWindingRatedVoltageInVolts / (100 * ratedApparentPowerInVoltsAmpere),
                            CimClasses.Reactance
                        )
                    )

                    else -> throw RuntimeException("Unexpected endNumber $endNumber")
                }

                equipment.libEquipmentId == EquipmentLibId.TWO_WINDING_AUTO_TRANSFORMER -> powerTransformerEndResource
                equipment.libEquipmentId == EquipmentLibId.THREE_WINDING_AUTO_TRANSFORMER -> powerTransformerEndResource

                else -> throw RuntimeException("Unexpected power transformer kind")
            }
        }
        .addObjectProperty(
            CimClasses.ConductingEquipment.BaseVoltage,
            baseVoltages[
                findNearestVoltageLevelByVoltageInKilovolts(ratedVoltageInKilovolts).id
            ]!!
        )
        .addObjectProperty(CimClasses.PowerTransformerEnd.PowerTransformer, powerTransformer)
        .apply {
            ratioTapChangerId?.let {
                addObjectProperty(CimClasses.TransformerEnd.RatioTapChanger, ratioTapChangerId)
            }
            terminals.forEach { terminal ->
                addObjectProperty(CimClasses.TransformerEnd.Terminal, terminal)
            }
        }.apply {
            if (equipment.libEquipmentId !== EquipmentLibId.THREE_WINDING_AUTO_TRANSFORMER) {
                addDataProperty(
                    CimClasses.PowerTransformerEnd.r,
                    UnitsConverter.withDefaultCimMultiplier(
                        equipment.getFieldDoubleValue(FieldLibId.SHORT_CIRCUIT_ACTIVE_POWER) * 1e3 *
                            ratedVoltageInVolts * ratedVoltageInVolts /
                            (ratedApparentPowerInVoltsAmpere * ratedApparentPowerInVoltsAmpere),
                        CimClasses.Resistance
                    )
                )
            }
        }.build()
}

private fun convertConnectionKind(
    windingType: WindingType
): CimEnum {
    return when (windingType) {
        WindingType.Y, WindingType.Yg -> CimClasses.WindingConnection.Y
        WindingType.D1, WindingType.D11 -> CimClasses.WindingConnection.D
    }
}

private fun findNearestVoltageLevelByVoltageInKilovolts(voltageInKilovolts: Double): VoltageLevelLib {
    val delta: (vl: VoltageLevelLib, vInKilovolts: Double) -> Double = { vl, vInKilovolts ->
        abs(vl.voltageInKilovolts - vInKilovolts)
    }

    return VoltageLevelLibId.values()
        .filter { it !== VoltageLevelLibId.UNRECOGNIZED }
        .map { EquipmentMetaInfoManager.getVoltageLevelById(it) }
        .reduce { acc, vl ->
            if (delta(vl, voltageInKilovolts) < delta(acc, voltageInKilovolts)) {
                vl
            } else {
                acc
            }
        }
}
