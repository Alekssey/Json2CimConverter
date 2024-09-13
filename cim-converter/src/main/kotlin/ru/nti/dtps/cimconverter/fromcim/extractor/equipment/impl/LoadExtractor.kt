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
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object LoadExtractor : SealedEquipmentExtractor() {

    private val cimClass = CimClasses.EnergyConsumer
    private val equipmentLibId = EquipmentLibId.LOAD

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
                cimClass.LoadResponse,
                cimClass.grounded,
                cimClass.pfixed,
                cimClass.p,
                cimClass.qfixed,
                cimClass.q,
                DtpsClasses.EnergyConsumer.ratedVoltage
            ),
            retrieveLoadResponseCharacteristics(
                repository.selectAllVarsFromTriples(
                    CimClasses.LoadResponseCharacteristic,
                    CimClasses.LoadResponseCharacteristic.pVoltageExponent,
                    CimClasses.LoadResponseCharacteristic.qVoltageExponent,
                    CimClasses.LoadResponseCharacteristic.qConstantPower
                )
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
        loadResponseCharacteristics: Map<String, LoadResponseCharacteristics>,
        substations: Map<String, RawSchemeDto.SubstationDto>,
        lines: Map<String, RawSchemeDto.TransmissionLineDto>,
        baseVoltages: Map<String, BaseVoltage>,
        voltageLevels: Map<String, VoltageLevel>,
        equipmentIdToPortsMap: Map<String, List<PortInfo>>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>,
        getEquipmentFrequencyOrDefault: (equipmentId: String) -> Double
    ): List<RawEquipmentNodeDto> = queryResult.mapIndexed { index, bindingSet ->
        val loadResponseCharacteristic = bindingSet
            .extractObjectReferenceOrNull(cimClass.LoadResponse)
            ?.let(loadResponseCharacteristics::get)

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
            FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to (
                bindingSet.extractDoubleValueOrNull(DtpsClasses.EnergyConsumer.ratedVoltage)
                    ?: bindingSet
                        .extractObjectReferenceOrNull(CimClasses.ConductingEquipment.BaseVoltage)
                        ?.let(baseVoltages::get)?.voltageLevelLib?.voltageInKilovolts
                ),

            FieldLibId.GROUNDED to if (
                bindingSet.extractBooleanValueOrFalse(cimClass.grounded)
            ) {
                "enabled"
            } else {
                "disabled"
            },

            FieldLibId.ACTIVE_POWER to (
                bindingSet.extractDoubleValueOrNull(cimClass.pfixed)
                    ?: bindingSet.extractDoubleValueOrNull(cimClass.p) ?: 10.0
                ),

            FieldLibId.REACTIVE_POWER to (
                bindingSet.extractDoubleValueOrNull(cimClass.qfixed)
                    ?: bindingSet.extractDoubleValueOrNull(cimClass.q) ?: 5.0
                ),

            FieldLibId.FREQUENCY to getEquipmentFrequencyOrDefault(bindingSet.extractIdentifiedObjectId()),
            FieldLibId.ACTIVE_POWER_TO_FREQUENCY_COEFFICIENT to loadResponseCharacteristic?.pVoltageExponent,
            FieldLibId.ACTIVE_POWER_TO_VOLTAGE_COEFFICIENT to loadResponseCharacteristic?.qVoltageExponent,
            FieldLibId.ACTIVE_POWER_TO_REACTIVE_POWER_COEFFICIENT to loadResponseCharacteristic?.qConstantPower
        )
    }

    private fun retrieveLoadResponseCharacteristics(
        queryResult: TupleQueryResult
    ): Map<String, LoadResponseCharacteristics> = queryResult.associate { bindingSet ->
        bindingSet.extractIdentifiedObjectId() to LoadResponseCharacteristics(
            pVoltageExponent = bindingSet.extractDoubleValueOrNull(
                CimClasses.LoadResponseCharacteristic.pVoltageExponent
            ),
            qVoltageExponent = bindingSet.extractDoubleValueOrNull(
                CimClasses.LoadResponseCharacteristic.qVoltageExponent
            ),
            qConstantPower = bindingSet.extractDoubleValueOrNull(
                CimClasses.LoadResponseCharacteristic.qConstantPower
            )
        )
    }

    private class LoadResponseCharacteristics(
        val pVoltageExponent: Double?,
        val qVoltageExponent: Double?,
        val qConstantPower: Double?
    )
}
