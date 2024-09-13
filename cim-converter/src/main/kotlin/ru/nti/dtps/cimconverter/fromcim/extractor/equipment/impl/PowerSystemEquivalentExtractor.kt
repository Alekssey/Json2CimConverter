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
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractDoubleValueOrNull
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIdentifiedObjectId
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses
import ru.nti.dtps.cimconverter.rectangularToPolar
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object PowerSystemEquivalentExtractor : SealedEquipmentExtractor() {

    private val cimClass = CimClasses.EquivalentInjection
    private val equipmentLibId = EquipmentLibId.POWER_SYSTEM_EQUIVALENT

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
                CimClasses.EquivalentInjection.r,
                CimClasses.EquivalentInjection.r2,
                CimClasses.EquivalentInjection.r0,
                CimClasses.EquivalentInjection.x,
                CimClasses.EquivalentInjection.x2,
                CimClasses.EquivalentInjection.x0,
                DtpsClasses.EquivalentInjection.emfTimeConstant
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
    ): List<RawEquipmentNodeDto> = queryResult.mapIndexed { index, bindingSet ->
        val resistancePosNegSeq =
            bindingSet.extractDoubleValueOrNull(CimClasses.EquivalentInjection.r)
                ?: bindingSet.extractDoubleValueOrNull(CimClasses.EquivalentInjection.r2)

        val reactancePosNegSeq =
            bindingSet.extractDoubleValueOrNull(CimClasses.EquivalentInjection.x)
                ?: bindingSet.extractDoubleValueOrNull(CimClasses.EquivalentInjection.x2)

        val magnitudeAndAngleOfImpedancePosNeqSeq = if (
            resistancePosNegSeq != null && reactancePosNegSeq != null
        ) {
            rectangularToPolar(resistancePosNegSeq, reactancePosNegSeq)
        } else {
            null
        }

        val resistanceZeroSeq = bindingSet.extractDoubleValueOrNull(CimClasses.EquivalentInjection.r0)

        val reactanceZeroSeq = bindingSet.extractDoubleValueOrNull(CimClasses.EquivalentInjection.x0)

        val magnitudeAndAngleOfImpedanceZeroSeq = if (
            resistanceZeroSeq != null && reactanceZeroSeq != null
        ) {
            rectangularToPolar(resistanceZeroSeq, reactanceZeroSeq)
        } else {
            null
        }

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
            FieldLibId.IMPEDANCE_POS_NEG_SEQ to magnitudeAndAngleOfImpedancePosNeqSeq?.first,
            FieldLibId.ANGLE_OF_IMPEDANCE_POS_NEG_SEQ to magnitudeAndAngleOfImpedancePosNeqSeq?.second,
            FieldLibId.IMPEDANCE_ZERO_SEQ to magnitudeAndAngleOfImpedanceZeroSeq?.first,
            FieldLibId.ANGLE_OF_IMPEDANCE_ZERO_SEQ to magnitudeAndAngleOfImpedanceZeroSeq?.second,
            FieldLibId.FREQUENCY to getEquipmentFrequencyOrDefault(bindingSet.extractIdentifiedObjectId()),
            FieldLibId.EMF_TIME_CONSTANT to bindingSet.extractDoubleValueOrNull(
                DtpsClasses.EquivalentInjection.emfTimeConstant
            )
        ).let {
            val portInfo = equipmentIdToPortsMap[bindingSet.extractIdentifiedObjectId()]?.firstOrNull()

            it.copyWithFields(
                FieldLibId.VOLTAGE_LINE_TO_LINE to portInfo?.terminal?.voltage?.magnitudeInKilovolts,
                FieldLibId.ANGLE_OF_PHASE_A to portInfo?.terminal?.voltage?.angleInDegree
            )
        }
    }
}
