package ru.nti.dtps.cimconverter.tocim.diagram.layout

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.tocim.equipment.EquipmentsRelatedResources
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.equipment.meta.info.extension.getSource
import ru.nti.dtps.equipment.meta.info.extension.getTarget
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.scheme.Scheme
import ru.nti.dtps.proto.scheme.link.EquipmentLink
import java.util.*

object DiagramLayoutConverter {
    fun convert(
        scheme: RawSchemeDto,
        equipmentsRelatedResources: EquipmentsRelatedResources
    ): List<RdfResource> {
        val diagram = createDiagram(scheme)

        val doesEquipmentBelongToCim: (equipmentId: String) -> Boolean = { equipmentId ->
            equipmentsRelatedResources.equipmentIdToResourceMap.containsKey(equipmentId)
        }

        val equipments = scheme.nodes.values
            .filter { it.libEquipmentId != EquipmentLibId.CONNECTIVITY }
            .filter { doesEquipmentBelongToCim(it.id) }

        val (equipmentDiagramObjects, equipmentDiagramObjectPoints) = EquipmentLayoutConverter
            .createEquipmentRelatedDiagramObjectAndPoints(
                scheme,
                equipments,
                diagram,
                equipmentsRelatedResources.equipmentIdToResourceMap,
                equipmentsRelatedResources.portIdToTerminalResourceMap
            )

        val (faultDiagramObjects, faultDiagramObjectPoints) = EquipmentLayoutConverter
            .createEquipmentRelatedDiagramObjectAndPoints(
                scheme,
                scheme.nodes.values.filter {
                    equipmentsRelatedResources.shortCircuitIdToFaultResources.containsKey(it.id)
                },
                diagram,
                equipmentsRelatedResources.shortCircuitIdToFaultResources,
                equipmentsRelatedResources.portIdToTerminalResourceMap
            )

        return listOf(diagram) +
            equipmentDiagramObjects +
            faultDiagramObjects +
            faultDiagramObjectPoints +
            equipmentDiagramObjectPoints
    }

    private fun createDiagram(scheme: RawSchemeDto): RdfResource = RdfResourceBuilder(
        UUID.randomUUID().toString(),
        CimClasses.Diagram
    ).apply {
        addEnumProperty(CimClasses.Diagram.orientation, CimClasses.OrientationKind.negative)
        addDataProperty(CimClasses.Diagram.x1InitialView, "0")
        addDataProperty(CimClasses.Diagram.y1InitialView, "0")
        addDataProperty(CimClasses.Diagram.x2InitialView, scheme.offsetX.toString())
        addDataProperty(CimClasses.Diagram.y2InitialView, scheme.offsetY.toString())
    }.build()

    private fun EquipmentLink.isLinkConnectsTwoConnectivities(scheme: Scheme): Boolean {
        return getSource(scheme).libEquipmentId == EquipmentLibId.CONNECTIVITY &&
            getTarget(scheme).libEquipmentId == EquipmentLibId.CONNECTIVITY
    }
}
