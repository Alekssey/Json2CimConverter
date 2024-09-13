package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.exception.CimDataException
import ru.nti.dtps.cimconverter.fromcim.extractor.EquipmentsExtractionResult
import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObject
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.RawEquipmentCreator
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.copyWithFields
import ru.nti.dtps.cimconverter.fromcim.extractor.link.PortInfo
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.basevoltage.BaseVoltage
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.voltagelevel.VoltageLevel
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractDoubleValueOrNull
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIntValueOrNull
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractObjectReferenceOrNull
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object CurrentTransformerExtractor : SealedEquipmentExtractor() {

    private val cimClass = CimClasses.CurrentTransformer
    private val equipmentLibId = EquipmentLibId.CURRENT_TRANSFORMER

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
    ) = EquipmentsExtractionResult(
        equipments = create(
            repository.selectAllVarsFromTriples(
                cimClass,
                CimClasses.IdentifiedObject.name,
                CimClasses.Equipment.EquipmentContainer,
                CimClasses.ConductingEquipment.BaseVoltage,
                DtpsClasses.CurrentTransformer.primaryWindingTurnAmount,
                DtpsClasses.CurrentTransformer.secondaryWindingTurnAmount,
                DtpsClasses.CurrentTransformer.coreCrossSection,
                DtpsClasses.CurrentTransformer.magneticPathAverageLength,
                DtpsClasses.CurrentTransformer.secondaryWindingResistance,
                DtpsClasses.CurrentTransformer.secondaryWindingInductance,
                DtpsClasses.CurrentTransformer.secondaryWindingLoadResistance,
                DtpsClasses.CurrentTransformer.secondaryWindingLoadInductance,
                DtpsClasses.CurrentTransformer.magnetizationCurveFirstCoefficient,
                DtpsClasses.CurrentTransformer.magnetizationCurveSecondCoefficient,
                DtpsClasses.CurrentTransformer.magnetizationCurveThirdCoefficient,
                DtpsClasses.CurrentTransformer.modelType
            ),
            substations,
            lines,
            baseVoltages,
            voltageLevels,
            equipmentIdToPortsMap,
            objectIdToDiagramObjectMap
        )
    )

    private fun create(
        queryResult: TupleQueryResult,
        substations: Map<String, RawSchemeDto.SubstationDto>,
        lines: Map<String, RawSchemeDto.TransmissionLineDto>,
        baseVoltages: Map<String, BaseVoltage>,
        voltageLevels: Map<String, VoltageLevel>,
        equipmentIdToPortsMap: Map<String, List<PortInfo>>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>
    ): List<RawEquipmentNodeDto> = queryResult.mapIndexed { index, bindingSet ->
        RawEquipmentCreator.equipmentWithBaseData(
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
            FieldLibId.PRIMARY_WINDING_TURN_AMOUNT to bindingSet
                .extractIntValueOrNull(DtpsClasses.CurrentTransformer.primaryWindingTurnAmount),
            FieldLibId.SECONDARY_WINDING_TURN_AMOUNT to bindingSet
                .extractIntValueOrNull(DtpsClasses.CurrentTransformer.secondaryWindingTurnAmount),
            FieldLibId.CORE_CROSS_SECTION to bindingSet
                .extractDoubleValueOrNull(DtpsClasses.CurrentTransformer.coreCrossSection),
            FieldLibId.MAGNETIC_PATH_AVERAGE_LENGTH to bindingSet
                .extractDoubleValueOrNull(DtpsClasses.CurrentTransformer.magneticPathAverageLength),
            FieldLibId.SECONDARY_WINDING_RESISTANCE to bindingSet
                .extractDoubleValueOrNull(DtpsClasses.CurrentTransformer.secondaryWindingResistance),
            FieldLibId.SECONDARY_WINDING_INDUCTANCE to bindingSet
                .extractDoubleValueOrNull(DtpsClasses.CurrentTransformer.secondaryWindingInductance),
            FieldLibId.SECONDARY_WINDING_LOAD_RESISTANCE to bindingSet
                .extractDoubleValueOrNull(DtpsClasses.CurrentTransformer.secondaryWindingLoadResistance),
            FieldLibId.SECONDARY_WINDING_LOAD_INDUCTANCE to bindingSet
                .extractDoubleValueOrNull(DtpsClasses.CurrentTransformer.secondaryWindingLoadInductance),
            FieldLibId.FIRST_COEFFICIENT_OF_MAGNETIZATION_CURVE to bindingSet
                .extractDoubleValueOrNull(DtpsClasses.CurrentTransformer.magnetizationCurveFirstCoefficient),
            FieldLibId.SECOND_COEFFICIENT_OF_MAGNETIZATION_CURVE to bindingSet
                .extractDoubleValueOrNull(DtpsClasses.CurrentTransformer.magnetizationCurveSecondCoefficient),
            FieldLibId.THIRD_COEFFICIENT_OF_MAGNETIZATION_CURVE to bindingSet
                .extractDoubleValueOrNull(DtpsClasses.CurrentTransformer.magnetizationCurveThirdCoefficient),
            FieldLibId.CURRENT_TRANSFORMER_MODEL to bindingSet
                .extractObjectReferenceOrNull(DtpsClasses.CurrentTransformer.modelType)
                ?.split(".")
                ?.getOrNull(1).let { modelType ->
                    when (modelType) {
                        "ideal" -> modelType
                        "real" -> "withSaturation"
                        else -> CimDataException("Значение поля ${DtpsClasses.CurrentTransformer.modelType.fullName} должно быть \"ideal\" или \"real\"")
                    }
                }
        )
    }
}
