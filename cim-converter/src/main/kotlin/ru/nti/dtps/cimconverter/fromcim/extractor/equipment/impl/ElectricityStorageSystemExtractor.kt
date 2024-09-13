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
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractStringValueOrNull
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

object ElectricityStorageSystemExtractor : SealedEquipmentExtractor() {

    private val cimClass = DtpsClasses.ElectricityStorageSystem
    private val equipmentLibId = EquipmentLibId.ELECTRICITY_STORAGE_SYSTEM

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
                cimClass.batteryСapacity,
                cimClass.maxCurrent,
                cimClass.polarizationConstant,
                cimClass.internalResistance,
                cimClass.initialCharge,
                cimClass.оperatingMode,
                DtpsClasses.EnergyConsumer.ratedVoltage
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
            FieldLibId.CAPACITY to bindingSet.extractDoubleValueOrNull(cimClass.batteryСapacity),
            FieldLibId.MAX_CURRENT to bindingSet.extractDoubleValueOrNull(cimClass.maxCurrent),
            FieldLibId.POLARIZATION_CONSTANT to bindingSet.extractDoubleValueOrNull(cimClass.polarizationConstant),
            FieldLibId.INTERNAL_RESISTANCE to bindingSet.extractDoubleValueOrNull(cimClass.internalResistance),
            FieldLibId.INITIAL_CHARGE_PERCENTAGE to bindingSet.extractDoubleValueOrNull(cimClass.initialCharge),
            FieldLibId.ELECTRICITY_STORAGE_SYSTEM_MODE to bindingSet
                .extractStringValueOrNull(cimClass.оperatingMode)
                ?.let(::extractElectricityStorageSystemMode),
            FieldLibId.ACTIVE_POWER_OF_CHARGE_DISCHARGE to bindingSet.extractDoubleValueOrNull(cimClass.activeChargeDischargePower)
        )
    }

    private fun extractElectricityStorageSystemMode(stringValue: String): String? {
        return when (val value = stringValue.split("ElectricityStorageSystemMode.").getOrNull(1)) {
            "charge", "discharge", "off" -> value
            else -> null
        }
    }
}
