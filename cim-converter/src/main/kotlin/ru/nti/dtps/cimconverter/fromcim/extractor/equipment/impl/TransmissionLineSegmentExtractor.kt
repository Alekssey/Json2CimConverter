package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.extractor.EquipmentsExtractionResult
import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObject
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.RawEquipmentCreator
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.copyWithFields
import ru.nti.dtps.cimconverter.fromcim.extractor.link.PortInfo
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.basevoltage.BaseVoltage
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.voltagelevel.VoltageLevel
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractBooleanValueOrFalse
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractDoubleValueOrNull
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIdentifiedObjectId
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractObjectReferenceOrNull
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.dto.scheme.getFieldDoubleValue
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object TransmissionLineSegmentExtractor : SealedEquipmentExtractor() {

    private val cimClass = CimClasses.ACLineSegment
    private val equipmentLibId = EquipmentLibId.TRANSMISSION_LINE_SEGMENT

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
                CimClasses.Conductor.length,
                CimClasses.ACLineSegment.r,
                CimClasses.ACLineSegment.r0,
                CimClasses.ACLineSegment.x,
                CimClasses.ACLineSegment.x0,
                CimClasses.ACLineSegment.bch,
                CimClasses.ACLineSegment.b0ch,
                DtpsClasses.ACLineSegment.ratedActivePower,
                DtpsClasses.ACLineSegment.useConcentratedParameters,
                DtpsClasses.ACLineSegment.DoubleCircuitTransmissionLineContainer
            ),
            substations,
            lines,
            baseVoltages,
            voltageLevels,
            equipmentIdToPortsMap,
            objectIdToDiagramObjectMap,
            getEquipmentFrequencyOrDefault
        )
    )

    private fun create(
        queryResult: TupleQueryResult,
        substations: Map<String, RawSchemeDto.SubstationDto>,
        lines: Map<String, RawSchemeDto.TransmissionLineDto>,
        baseVoltages: Map<String, BaseVoltage>,
        voltageLevels: Map<String, VoltageLevel>,
        equipmentIdToPortsMap: Map<String, List<PortInfo>>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>,
        getEquipmentFrequencyOrDefault: (equipmentId: String) -> Double
    ): List<RawEquipmentNodeDto> = queryResult
        .toList()
        .filter {
            it.extractObjectReferenceOrNull(DtpsClasses.ACLineSegment.DoubleCircuitTransmissionLineContainer) == null
        }
        .mapIndexed { index, bindingSet ->
            val length = bindingSet.extractDoubleValueOrNull(CimClasses.Conductor.length)?.let {
                if (it == 0.0) 1.0 else it
            } ?: 1.0

            fun divideByLength(value: Double): Double = value / length

            val equipment = RawEquipmentCreator.equipmentWithBaseData(
                bindingSet,
                substations,
                lines,
                baseVoltages,
                voltageLevels,
                index + 1,
                equipmentLibId,
                equipmentIdToPortsMap,
                objectIdToDiagramObjectMap
            )

            val resistancePosNeqSeq = divideByLength(
                bindingSet.extractDoubleValueOrNull(CimClasses.ACLineSegment.r)
                    ?: equipment.getFieldDoubleValue(FieldLibId.RESISTANCE_PER_LENGTH_POS_NEG_SEQ)
            )

            val resistanceZeroSeq = divideByLength(
                bindingSet.extractDoubleValueOrNull(CimClasses.ACLineSegment.r0)
                    ?: equipment.getFieldDoubleValue(FieldLibId.RESISTANCE_PER_LENGTH_ZERO_SEQ)
            )

            val reactancePosNeqSeq = divideByLength(
                bindingSet.extractDoubleValueOrNull(CimClasses.ACLineSegment.x)
                    ?: equipment.getFieldDoubleValue(FieldLibId.REACTANCE_PER_LENGTH_POS_NEG_SEQ)
            )

            val reactanceZeroSeq = divideByLength(
                bindingSet.extractDoubleValueOrNull(CimClasses.ACLineSegment.x0)
                    ?: equipment.getFieldDoubleValue(FieldLibId.REACTANCE_PER_LENGTH_ZERO_SEQ)
            )

            val susceptancePosNeqSeq = divideByLength(
                bindingSet.extractDoubleValueOrNull(CimClasses.ACLineSegment.bch)
                    ?: equipment.getFieldDoubleValue(FieldLibId.SUSCEPTANCE_PER_LENGTH_POS_NEG_SEQ)
            )

            val susceptanceZeroSeq = divideByLength(
                bindingSet.extractDoubleValueOrNull(CimClasses.ACLineSegment.b0ch)
                    ?: equipment.getFieldDoubleValue(FieldLibId.SUSCEPTANCE_PER_LENGTH_ZERO_SEQ)
            )

            equipment.copyWithFields(
                FieldLibId.LENGTH to length,
                FieldLibId.RESISTANCE_PER_LENGTH_POS_NEG_SEQ to resistancePosNeqSeq,
                FieldLibId.RESISTANCE_PER_LENGTH_ZERO_SEQ to when {
                    resistanceZeroSeq < resistancePosNeqSeq -> resistancePosNeqSeq
                    else -> resistanceZeroSeq
                },
                FieldLibId.REACTANCE_PER_LENGTH_POS_NEG_SEQ to reactancePosNeqSeq,
                FieldLibId.REACTANCE_PER_LENGTH_ZERO_SEQ to when {
                    reactanceZeroSeq < reactancePosNeqSeq -> reactancePosNeqSeq
                    else -> reactanceZeroSeq
                },
                FieldLibId.SUSCEPTANCE_PER_LENGTH_POS_NEG_SEQ to when {
                    susceptancePosNeqSeq < susceptanceZeroSeq -> susceptanceZeroSeq
                    else -> susceptancePosNeqSeq
                },
                FieldLibId.SUSCEPTANCE_PER_LENGTH_ZERO_SEQ to susceptanceZeroSeq,
                FieldLibId.FREQUENCY to getEquipmentFrequencyOrDefault(bindingSet.extractIdentifiedObjectId()),
                FieldLibId.VOLTAGE_LEVEL to bindingSet
                    .extractObjectReferenceOrNull(CimClasses.ConductingEquipment.BaseVoltage)
                    ?.let(baseVoltages::get)?.voltageLevelLib?.id,
                FieldLibId.RATED_ACTIVE_POWER to bindingSet.extractDoubleValueOrNull(
                    DtpsClasses.ACLineSegment.ratedActivePower
                ),
                FieldLibId.USE_CONCENTRATED_PARAMETERS to if (bindingSet.extractBooleanValueOrFalse(
                        DtpsClasses.ACLineSegment.useConcentratedParameters
                    )
                ) {
                    "enabled"
                } else {
                    "disabled"
                }
            )
        }
}
