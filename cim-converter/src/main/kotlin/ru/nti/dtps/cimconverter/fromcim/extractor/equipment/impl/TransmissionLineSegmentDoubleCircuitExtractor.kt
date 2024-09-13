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
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractBooleanValueOrFalse
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractDoubleValueOrNull
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIdentifiedObjectId
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractObjectReferenceOrNull
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses
import ru.nti.dtps.dto.scheme.*
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.port.PortLibId

object TransmissionLineSegmentDoubleCircuitExtractor : SealedEquipmentExtractor() {

    private val cimClass = DtpsClasses.DoubleCircuitTransmissionLineSegmentContainer
    private val equipmentLibId = EquipmentLibId.TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT

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
        val doubleCircuitContainerIdToTransmissionLineSegmentsMap = createTransmissionLineSegments(
            repository.selectAllVarsFromTriples(
                CimClasses.ACLineSegment,
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

        return create(
            repository.selectAllVarsFromTriples(
                cimClass,
                CimClasses.IdentifiedObject.name
            ),
            doubleCircuitContainerIdToTransmissionLineSegmentsMap,
            substations,
            lines,
            baseVoltages,
            voltageLevels,
            equipmentIdToPortsMap,
            objectIdToDiagramObjectMap,
            links
        )
    }

    private fun create(
        queryResult: TupleQueryResult,
        doubleCircuitContainerIdToTransmissionLineSegmentsMap: Map<String, List<RawEquipmentNodeDto>>,
        substations: Map<String, RawSchemeDto.SubstationDto>,
        lines: Map<String, RawSchemeDto.TransmissionLineDto>,
        baseVoltages: Map<String, BaseVoltage>,
        voltageLevels: Map<String, VoltageLevel>,
        equipmentIdToPortsMap: Map<String, List<PortInfo>>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>,
        links: List<RawEquipmentLinkDto>
    ): EquipmentsExtractionResult {
        val equipments: MutableList<RawEquipmentNodeDto> = mutableListOf()
        val updatedLinks: MutableList<RawEquipmentLinkDto> = mutableListOf()

        queryResult.mapIndexed { index, bindingSet ->
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

            val transmissionLineSegments = doubleCircuitContainerIdToTransmissionLineSegmentsMap[equipment.id]
            val transmissionLineSegmentNumber = transmissionLineSegments?.size ?: 0

            if (transmissionLineSegments == null || transmissionLineSegmentNumber != 2) {
                throw CimDataException("Double circuit container ${equipment.id} should contain 2 transmission line segments, but contains $transmissionLineSegmentNumber segments")
            }

            val firstTransmissionLineSegment = transmissionLineSegments[0]
            val secondTransmissionLineSegment = transmissionLineSegments[1]

            equipments += equipment.copy(
                voltageLevelId = firstTransmissionLineSegment.voltageLevelId,
                fields = equipment.fields + mapOf(
                    FieldLibId.VOLTAGE_LEVEL to firstTransmissionLineSegment
                        .getFieldStringValueOrNull(FieldLibId.VOLTAGE_LEVEL),
                    FieldLibId.FIRST_CIRCUIT_TRANSMISSION_LINE to firstTransmissionLineSegment
                        .getFieldStringValueOrNull(FieldLibId.TRANSMISSION_LINE),
                    FieldLibId.SECOND_CIRCUIT_TRANSMISSION_LINE to secondTransmissionLineSegment
                        .getFieldStringValueOrNull(FieldLibId.TRANSMISSION_LINE),
                    FieldLibId.LENGTH to firstTransmissionLineSegment
                        .getFieldStringValueOrNull(FieldLibId.LENGTH),
                    FieldLibId.USE_CONCENTRATED_PARAMETERS to firstTransmissionLineSegment
                        .getFieldStringValueOrNull(FieldLibId.USE_CONCENTRATED_PARAMETERS),
                    FieldLibId.RESISTANCE_PER_LENGTH_POS_NEG_SEQ to firstTransmissionLineSegment
                        .getFieldStringValueOrNull(FieldLibId.RESISTANCE_PER_LENGTH_POS_NEG_SEQ),
                    FieldLibId.REACTANCE_PER_LENGTH_POS_NEG_SEQ to firstTransmissionLineSegment
                        .getFieldStringValueOrNull(FieldLibId.REACTANCE_PER_LENGTH_POS_NEG_SEQ),
                    FieldLibId.SUSCEPTANCE_PER_LENGTH_POS_NEG_SEQ to firstTransmissionLineSegment
                        .getFieldStringValueOrNull(FieldLibId.SUSCEPTANCE_PER_LENGTH_POS_NEG_SEQ),
                    FieldLibId.RESISTANCE_PER_LENGTH_ZERO_SEQ to firstTransmissionLineSegment
                        .getFieldStringValueOrNull(FieldLibId.RESISTANCE_PER_LENGTH_ZERO_SEQ),
                    FieldLibId.REACTANCE_PER_LENGTH_ZERO_SEQ to firstTransmissionLineSegment
                        .getFieldStringValueOrNull(FieldLibId.REACTANCE_PER_LENGTH_ZERO_SEQ),
                    FieldLibId.SUSCEPTANCE_PER_LENGTH_ZERO_SEQ to firstTransmissionLineSegment
                        .getFieldStringValueOrNull(FieldLibId.SUSCEPTANCE_PER_LENGTH_ZERO_SEQ),
                    FieldLibId.RATED_ACTIVE_POWER to firstTransmissionLineSegment
                        .getFieldStringValueOrNull(FieldLibId.RATED_ACTIVE_POWER)
                ),
                ports = (
                    firstTransmissionLineSegment.ports + secondTransmissionLineSegment.ports.map { port ->
                        port.copy(
                            libId = when (port.libId) {
                                PortLibId.FIRST -> PortLibId.THIRD
                                PortLibId.SECOND -> PortLibId.FOURTH
                                else -> throw RuntimeException("Unexpected port lib id ${port.libId}")
                            }
                        )
                    }
                    ).map { port -> port.copy(parentNode = equipment.id) }
            )

            fun updateTransmissionLineSegmentRelatedLinks(segment: RawEquipmentNodeDto) {
                segment.ports
                    .flatMap { port -> port.links }
                    .map { linkId -> links.first { it.id == linkId } }
                    .map { link ->
                        link.copy(
                            source = if (link.source == segment.id) {
                                equipment.id
                            } else {
                                link.source
                            },
                            target = if (link.target == segment.id) {
                                equipment.id
                            } else {
                                link.target
                            }
                        )
                    }.forEach(updatedLinks::add)
            }

            updateTransmissionLineSegmentRelatedLinks(firstTransmissionLineSegment)
            updateTransmissionLineSegmentRelatedLinks(secondTransmissionLineSegment)
        }

        return EquipmentsExtractionResult(
            equipments = equipments,
            updatedLinks = updatedLinks
        )
    }

    private fun createTransmissionLineSegments(
        queryResult: TupleQueryResult,
        substations: Map<String, RawSchemeDto.SubstationDto>,
        lines: Map<String, RawSchemeDto.TransmissionLineDto>,
        baseVoltages: Map<String, BaseVoltage>,
        voltageLevels: Map<String, VoltageLevel>,
        equipmentIdToPortsMap: Map<String, List<PortInfo>>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>,
        getEquipmentFrequencyOrDefault: (equipmentId: String) -> Double
    ): Map<String, List<RawEquipmentNodeDto>> = queryResult
        .toList()
        .filter {
            it.extractObjectReferenceOrNull(DtpsClasses.ACLineSegment.DoubleCircuitTransmissionLineContainer) != null
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
                EquipmentLibId.TRANSMISSION_LINE_SEGMENT,
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

            val doubleCircuitTransmissionLineContainerId = bindingSet
                .extractObjectReferenceOrNull(DtpsClasses.ACLineSegment.DoubleCircuitTransmissionLineContainer)!!

            doubleCircuitTransmissionLineContainerId to equipment.copyWithFields(
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
        }.groupBy(keySelector = { it.first }, valueTransform = { it.second })
}
