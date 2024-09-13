package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl

import ru.nti.dtps.cimconverter.fromcim.extractor.EquipmentsExtractionResult
import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObject
import ru.nti.dtps.cimconverter.fromcim.extractor.link.PortInfo
import ru.nti.dtps.cimconverter.fromcim.extractor.terminal.Terminal
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.basevoltage.BaseVoltage
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.voltagelevel.VoltageLevel
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId

sealed class SealedEquipmentExtractor {

    abstract fun extract(
        repository: RdfRepository,
        substations: Map<String, RawSchemeDto.SubstationDto>,
        lines: Map<String, RawSchemeDto.TransmissionLineDto>,
        baseVoltages: Map<String, BaseVoltage>,
        voltageLevels: Map<String, VoltageLevel>,
        equipmentIdToPortsMap: Map<String, List<PortInfo>>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>,
        links: List<RawEquipmentLinkDto>,
        getEquipmentFrequencyOrDefault: (equipmentId: String) -> Double
    ): EquipmentsExtractionResult

    protected fun defineEquipmentLibId(
        threePhaseEquipmentLibId: EquipmentLibId,
        singlePhaseEquipmentLibId: EquipmentLibId,
        equipmentPorts: List<PortInfo>?
    ): EquipmentLibId = equipmentPorts?.let { ports ->
        if (
            ports
                .mapNotNull(PortInfo::terminal)
                .mapNotNull(Terminal::phaseCode)
                .any(CimClasses.PhaseCode.singlePhaseCodes::contains)
        ) {
            singlePhaseEquipmentLibId
        } else {
            threePhaseEquipmentLibId
        }
    } ?: threePhaseEquipmentLibId
}
