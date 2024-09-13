package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.ext.isThreeWindingPowerTransformer
import ru.nti.dtps.cimconverter.ext.isTwoWindingPowerTransformer
import ru.nti.dtps.cimconverter.fromcim.exception.CimDataException
import ru.nti.dtps.cimconverter.fromcim.extractor.EquipmentsExtractionResult
import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObject
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.RawEquipmentCreator
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.copyWithFields
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.transformer.PowerTransformerEnd
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.transformer.PowerTransformerEndExtractor
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.transformer.TapChangerExtractor
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.transformer.auto.ThreeWindingAutoTransformerOps
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.transformer.auto.TwoWindingAutoTransformerOps
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.transformer.ordinary.ThreeWindingPowerTransformerOps
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.transformer.ordinary.TwoWindingPowerTransformerOps
import ru.nti.dtps.cimconverter.fromcim.extractor.link.PortInfo
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.basevoltage.BaseVoltage
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.voltagelevel.VoltageLevel
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractDoubleValueOrNull
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIdentifiedObjectId
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId
import java.lang.IllegalArgumentException

object PowerTransformerExtractor : SealedEquipmentExtractor() {

    private val cimClass = CimClasses.PowerTransformer

    override fun extract(
        repository: RdfRepository,
        substations: Map<String, RawSchemeDto.SubstationDto>,
        lines: Map<String, RawSchemeDto.TransmissionLineDto>,
        baseVoltages: Map<String, BaseVoltage>,
        voltageLevels: Map<String, VoltageLevel>,
        equipmentIdToPortsMap: Map<String, List<PortInfo>>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>,
        links: List<RawEquipmentLinkDto>,
        getEquipmentFrequencyOrDefault: (equipmentId: String) -> Double
    ): EquipmentsExtractionResult {
        val powerTransformerIdToPowerTransformerEnds: Map<String, List<PowerTransformerEnd>> =
            PowerTransformerEndExtractor.extract(repository, baseVoltages, TapChangerExtractor.extract(repository))

        return EquipmentsExtractionResult(
            equipments = create(
                repository.selectAllVarsFromTriples(
                    cimClass,
                    CimClasses.IdentifiedObject.name,
                    CimClasses.Equipment.EquipmentContainer,
                    CimClasses.ConductingEquipment.BaseVoltage,
                    CimClasses.TransformerEnd.magBaseU,
                    CimClasses.TransformerEnd.magSatFlux,
                    CimClasses.TransformerEnd.bmagSat
                ),
                substations,
                lines,
                baseVoltages,
                voltageLevels,
                equipmentIdToPortsMap,
                objectIdToDiagramObjectMap,
                getEquipmentFrequencyOrDefault,
                powerTransformerIdToPowerTransformerEnds
            )
        )
    }

    private fun create(
        queryResult: TupleQueryResult,
        substations: Map<String, RawSchemeDto.SubstationDto>,
        lines: Map<String, RawSchemeDto.TransmissionLineDto>,
        baseVoltages: Map<String, BaseVoltage>,
        voltageLevels: Map<String, VoltageLevel>,
        equipmentIdToPortsMap: Map<String, List<PortInfo>>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>,
        getEquipmentFrequencyOrDefault: (equipmentId: String) -> Double,
        powerTransformerIdToPowerTransformerEnds: Map<String, List<PowerTransformerEnd>>
    ): List<RawEquipmentNodeDto> {
        val equipments = queryResult.mapIndexedNotNull { index, bindingSet ->
            val id = bindingSet.extractIdentifiedObjectId()
            powerTransformerIdToPowerTransformerEnds[id]?.let { powerTransformerEnds ->
                val powerTransformerEndWithTapChanger = powerTransformerEnds.firstOrNull { it.tapChanger != null }
                val firstWindingPowerTransformerEnd = getFirstWindingPowerTransformerEndOrNull(
                    powerTransformerEnds
                )

                val secondWindingPowerTransformerEnd = getSecondWindingPowerTransformerEndOrNull(
                    powerTransformerEnds
                )

                val thirdWindingPowerTransformerEnd = getThirdWindingPowerTransformerEndOrNull(
                    powerTransformerEnds
                )

                val equipmentLibId = chooseEquipmentLibIdOrNull(
                    equipmentIdToPortsMap[id]?.size ?: 0,
                    powerTransformerEnds
                ) ?: throw CimDataException(
                    "Unexpected amount of power transformer ends (${powerTransformerEnds.size}) " +
                        "for transformer $id"
                )

                val powerTransformerEquipment = RawEquipmentCreator.equipmentWithBaseData(
                    bindingSet,
                    substations,
                    lines,
                    baseVoltages,
                    voltageLevels,
                    index + 1,
                    equipmentLibId,
                    equipmentIdToPortsMap,
                    objectIdToDiagramObjectMap
                ).copyWithFields(
                    FieldLibId.RATED_APPARENT_POWER to firstWindingPowerTransformerEnd?.ratedS,
                    FieldLibId.FIRST_WINDING_RATED_VOLTAGE to firstWindingPowerTransformerEnd?.ratedU,
                    FieldLibId.SECOND_WINDING_RATED_VOLTAGE to secondWindingPowerTransformerEnd?.ratedU,
                    FieldLibId.THIRD_WINDING_RATED_VOLTAGE to thirdWindingPowerTransformerEnd?.ratedU,
                    FieldLibId.FIRST_WINDING_TYPE to firstWindingPowerTransformerEnd?.windingType
                        ?.toString()?.lowercase(),
                    FieldLibId.SECOND_WINDING_TYPE to secondWindingPowerTransformerEnd?.windingType
                        ?.toString()?.lowercase(),
                    FieldLibId.THIRD_WINDING_TYPE to thirdWindingPowerTransformerEnd?.windingType
                        ?.toString()?.lowercase(),
                    FieldLibId.MAGNETIZATION_VOLTAGE to bindingSet.extractDoubleValueOrNull(
                        CimClasses.TransformerEnd.magBaseU
                    ),
                    FieldLibId.SATURATION_EXIST to when {
                        firstWindingPowerTransformerEnd?.saturationExists ?: false -> "onFirstWinding"
                        secondWindingPowerTransformerEnd?.saturationExists ?: false -> "onSecondWinding"
                        thirdWindingPowerTransformerEnd?.saturationExists ?: false -> "onThirdWinding"
                        else -> "no"
                    },
                    FieldLibId.AIR_CORE_RESISTANCE to bindingSet.extractDoubleValueOrNull(
                        CimClasses.TransformerEnd.bmagSat
                    ),
                    FieldLibId.SATURATION_COEFFICIENT to bindingSet.extractDoubleValueOrNull(
                        CimClasses.TransformerEnd.magSatFlux
                    )?.let { it * 1e-2 },
                    FieldLibId.FREQUENCY to getEquipmentFrequencyOrDefault(bindingSet.extractIdentifiedObjectId())
                ).let {
                    if (powerTransformerEndWithTapChanger != null) {
                        val tapChanger = powerTransformerEndWithTapChanger.tapChanger!!
                        it.copyWithFields(
                            FieldLibId.TAP_CHANGER_EXISTENCE to "enabled",
                            FieldLibId.TAP_CHANGER_INSTALLATION_WINDING to when {
                                powerTransformerEndWithTapChanger.isFirstWinding() -> "onFirstWinding"
                                powerTransformerEndWithTapChanger.isSecondWinding() -> "onSecondWinding"
                                powerTransformerEndWithTapChanger.isThirdWinding() -> "onThirdWinding"
                                else -> null
                            },
                            FieldLibId.TAP_CHANGER_DEFAULT_POSITION to when {
                                tapChanger.defaultPosition < tapChanger.minPosition -> tapChanger.minPosition
                                tapChanger.defaultPosition > tapChanger.maxPosition -> tapChanger.maxPosition
                                else -> tapChanger.defaultPosition
                            },
                            FieldLibId.TAP_CHANGER_MAX_POSITION to tapChanger.maxPosition,
                            FieldLibId.TAP_CHANGER_MIN_POSITION to tapChanger.minPosition,
                            FieldLibId.TAP_CHANGER_VOLTAGE_CHANGE to tapChanger.voltageChange
                        )
                    } else {
                        it.copyWithFields(
                            FieldLibId.TAP_CHANGER_EXISTENCE to "disabled"
                        )
                    }
                }

                when {
                    equipmentLibId.isTwoWindingPowerTransformer() -> TwoWindingPowerTransformerOps.copyWithSpecificFields(
                        powerTransformerEquipment,
                        firstWindingPowerTransformerEnd
                    )

                    equipmentLibId.isThreeWindingPowerTransformer() -> ThreeWindingPowerTransformerOps.copyWithSpecificFields(
                        powerTransformerEquipment,
                        firstWindingPowerTransformerEnd,
                        secondWindingPowerTransformerEnd,
                        thirdWindingPowerTransformerEnd
                    )

                    equipmentLibId == EquipmentLibId.TWO_WINDING_AUTO_TRANSFORMER -> TwoWindingAutoTransformerOps.copyWithSpecificFields(
                        powerTransformerEquipment,
                        firstWindingPowerTransformerEnd
                    )

                    equipmentLibId == EquipmentLibId.THREE_WINDING_AUTO_TRANSFORMER -> ThreeWindingAutoTransformerOps.copyWithSpecificFields(
                        powerTransformerEquipment,
                        firstWindingPowerTransformerEnd,
                        secondWindingPowerTransformerEnd,
                        thirdWindingPowerTransformerEnd
                    )

                    else -> throw IllegalArgumentException("Unexpected equipment lib id: $equipmentLibId")
                }
            }
        }

        return equipments
    }

    private fun getFirstWindingPowerTransformerEndOrNull(
        powerTransformerEnds: List<PowerTransformerEnd>
    ): PowerTransformerEnd? {
        return getPowerTransformerEndWithEndNumberOrNull(powerTransformerEnds, 1)
    }

    private fun getSecondWindingPowerTransformerEndOrNull(
        powerTransformerEnds: List<PowerTransformerEnd>
    ): PowerTransformerEnd? {
        return getPowerTransformerEndWithEndNumberOrNull(powerTransformerEnds, 2)
    }

    private fun getThirdWindingPowerTransformerEndOrNull(
        powerTransformerEnds: List<PowerTransformerEnd>
    ): PowerTransformerEnd? {
        return getPowerTransformerEndWithEndNumberOrNull(powerTransformerEnds, 3)
    }

    private fun getPowerTransformerEndWithEndNumberOrNull(
        powerTransformerEnds: List<PowerTransformerEnd>,
        endNumber: Int
    ): PowerTransformerEnd? {
        return powerTransformerEnds.firstOrNull { it.endNumber == endNumber }
    }

    private fun chooseEquipmentLibIdOrNull(
        portsAmount: Int,
        powerTransformerEnds: List<PowerTransformerEnd>
    ): EquipmentLibId? = when (portsAmount) {
        2 -> when (powerTransformerEnds.size) {
            1 -> EquipmentLibId.TWO_WINDING_AUTO_TRANSFORMER
            2 -> EquipmentLibId.TWO_WINDING_POWER_TRANSFORMER
            else -> null
        }

        3 -> when (powerTransformerEnds.size) {
            2 -> EquipmentLibId.THREE_WINDING_AUTO_TRANSFORMER
            3 -> EquipmentLibId.THREE_WINDING_POWER_TRANSFORMER
            else -> null
        }

        else -> null
    }
}
