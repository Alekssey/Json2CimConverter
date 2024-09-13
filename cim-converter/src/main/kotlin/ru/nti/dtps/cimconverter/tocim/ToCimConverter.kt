package ru.nti.dtps.cimconverter.tocim

import ru.nti.dtps.cimconverter.rdf.RdfBuilder
import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.tocim.connectivity.ConnectivityNodeConverter
import ru.nti.dtps.cimconverter.tocim.diagram.layout.DiagramLayoutConverter
import ru.nti.dtps.cimconverter.tocim.equipment.EquipmentConverter
import ru.nti.dtps.cimconverter.tocim.equipment.EquipmentsRelatedResources
import ru.nti.dtps.cimconverter.tocim.frequency.FrequencyConverter
import ru.nti.dtps.cimconverter.tocim.line.TransmissionLineConverter
import ru.nti.dtps.cimconverter.tocim.substation.SubstationConverter
import ru.nti.dtps.cimconverter.tocim.sv.SvVoltageConverter
import ru.nti.dtps.cimconverter.tocim.topological.TopologicalNodeConverter
import ru.nti.dtps.cimconverter.tocim.votlage.base.BaseVoltageConverter
import ru.nti.dtps.cimconverter.tocim.votlage.level.VoltageLevelConverter
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId

object ToCimConverter {

    fun toCim(scheme: RawSchemeDto): String {
        val resources = createRdfResources(scheme)
        return RdfBuilder.build(resources)
    }

    private fun createRdfResources(scheme: RawSchemeDto): Set<RdfResource> {
        val substations: Map<String, RdfResource> = SubstationConverter.convert(scheme)
        val lines: Map<String, RdfResource> = TransmissionLineConverter.convert(scheme)
        val linkIdToTnMap: Map<String, RdfResource> = TopologicalNodeConverter.convertAndReturnLinkIdToTnMap(scheme)
        val linkIdToCnMap: Map<String, RdfResource> = ConnectivityNodeConverter.convertAndReturnLinkIdToCnMap(
            scheme,
            linkIdToTnMap
        )
        val svVoltages: List<RdfResource> = SvVoltageConverter.convert(scheme, linkIdToTnMap)
        val baseVoltages: Map<VoltageLevelLibId, RdfResource> = BaseVoltageConverter.convert(scheme)
        val (
            voltageLevelIdToVoltageLevelResourceMap,
            voltageLevelIdToEquipmentsMap
        ) = VoltageLevelConverter.convert(scheme, substations, baseVoltages)

        val equipmentsRelatedResources: EquipmentsRelatedResources = EquipmentConverter.convert(
            scheme = scheme,
            lines = lines,
            baseVoltages = baseVoltages,
            voltageLevelIdToVoltageLevelResourceMap = voltageLevelIdToVoltageLevelResourceMap,
            voltageLevelIdToEquipmentsMap = voltageLevelIdToEquipmentsMap,
            linkIdToTnMap = linkIdToTnMap,
            linkIdToCnMap = linkIdToCnMap
        )

        val diagramLayoutResources: List<RdfResource> = DiagramLayoutConverter.convert(
            scheme,
            equipmentsRelatedResources
        )

        val frequencies = FrequencyConverter.convert(scheme, diagramLayoutResources)

        val resources = substations.values +
            lines.values +
            frequencies +
            baseVoltages.values +
            voltageLevelIdToVoltageLevelResourceMap.values +
            equipmentsRelatedResources.equipmentIdToResourceMap.values +
            equipmentsRelatedResources.equipmentAdditionalResources +
            equipmentsRelatedResources.portIdToTerminalResourceMap.values +
            equipmentsRelatedResources.shortCircuitIdToFaultResources.values +
            linkIdToTnMap.values +
            linkIdToCnMap.values +
            svVoltages +
            diagramLayoutResources

        return resources.toSet()
    }
}
