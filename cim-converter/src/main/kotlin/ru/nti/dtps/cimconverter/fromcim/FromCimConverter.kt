package ru.nti.dtps.cimconverter.fromcim

import ru.nti.dtps.cimconverter.fromcim.extractor.auxiliary.AuxiliaryEquipmentLocationDefiner
import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObjectExtractor
import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObjectPointExtractor
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.EquipmentsExtractor
import ru.nti.dtps.cimconverter.fromcim.extractor.frequency.FrequencyExtractor
import ru.nti.dtps.cimconverter.fromcim.extractor.lines.LinesExtractor
import ru.nti.dtps.cimconverter.fromcim.extractor.link.LinksAndPortsCreator
import ru.nti.dtps.cimconverter.fromcim.extractor.link.OrphanLinksFilter
import ru.nti.dtps.cimconverter.fromcim.extractor.link.pointscalculator.PointsCalculator
import ru.nti.dtps.cimconverter.fromcim.extractor.reducer.SchemeReducer
import ru.nti.dtps.cimconverter.fromcim.extractor.substation.SubstationExtractor
import ru.nti.dtps.cimconverter.fromcim.extractor.terminal.TerminalExtractor
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.basevoltage.BaseVoltageExtractor
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.voltagelevel.VoltageLevelExtractor
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.runWithRdfRepository
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.dto.scheme.RawSchemeMapper
import java.io.InputStream

object FromCimConverter {

    fun fromCim(
        cimXmlFileInputStream: InputStream
    ): String = runWithRdfRepository(cimXmlFileInputStream) { repository ->
        val scheme = readRdfTriplesAndCreateScheme(repository)
        RawSchemeMapper.toJsonString(scheme)
    }

    private fun readRdfTriplesAndCreateScheme(repository: RdfRepository): RawSchemeDto {
        val substations = SubstationExtractor.extract(repository)
        val lines = LinesExtractor.extract(repository)
        val baseVoltages = BaseVoltageExtractor.extract(repository)
        val voltageLevels = VoltageLevelExtractor.extract(
            repository,
            substations,
            baseVoltages
        )

        val objectIdToDiagramObjectMap = DiagramObjectExtractor.extract(
            repository,
            DiagramObjectPointExtractor.extract(repository)
        )

        val connectivityNodeIdToTerminalsMap = AuxiliaryEquipmentLocationDefiner.createAuxiliaryEquipmentTerminals(
            repository,
            TerminalExtractor.createConnectivityNodeIdToTerminalsMap(repository)
        )

        val (links, equipmentIdToPortsInfoMap, connectivities) = LinksAndPortsCreator.create(
            repository,
            connectivityNodeIdToTerminalsMap,
            objectIdToDiagramObjectMap
        )

        val equipmentIdToFrequencyMap = FrequencyExtractor.extract(repository)

        val (equipments, updatedLinks) = EquipmentsExtractor.extract(
            repository,
            substations,
            lines,
            baseVoltages,
            voltageLevels,
            equipmentIdToPortsInfoMap,
            objectIdToDiagramObjectMap,
            links,
            getEquipmentFrequencyOrDefault = { equipmentId -> equipmentIdToFrequencyMap[equipmentId] ?: 50.0 }
        )

        val nodes = equipments.associateBy(RawEquipmentNodeDto::id) +
            connectivities.associateBy(RawEquipmentNodeDto::id)

        return RawSchemeDto(
            offsetX = 140.0,
            offsetY = 140.0,
            zoom = 23.0,
            substations = substations.values.toList(),
            transmissionLines = lines.values.toList(),
            nodes = nodes,
            links = PointsCalculator.calculateLinkPointsAndReturnLinks(
                allLinks = OrphanLinksFilter.filter(updatedLinks, nodes),
                equipments = nodes
            ).associateBy(RawEquipmentLinkDto::id)
        ).let { scheme -> SchemeReducer.reduce(repository, scheme) }
    }
}
