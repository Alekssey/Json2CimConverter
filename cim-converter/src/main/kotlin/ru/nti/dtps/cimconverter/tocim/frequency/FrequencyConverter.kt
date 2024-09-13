package ru.nti.dtps.cimconverter.tocim.frequency

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.dto.scheme.getFieldDoubleValue
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId
import java.util.UUID

private val equipmentsWithFrequency = setOf(
    EquipmentLibId.POWER_SYSTEM_EQUIVALENT,
    EquipmentLibId.TWO_WINDING_POWER_TRANSFORMER,
    EquipmentLibId.THREE_WINDING_POWER_TRANSFORMER,
    EquipmentLibId.TRANSMISSION_LINE_SEGMENT,
    EquipmentLibId.LOAD,
    EquipmentLibId.REACTIVE_POWER_COMPENSATION_SYSTEM
)

object FrequencyConverter {

    fun convert(scheme: RawSchemeDto, diagramLayoutResources: List<RdfResource>): Set<RdfResource> {
        val identifiedObjectIdToDiagramLayoutResourceMap: Map<String?, RdfResource> = diagramLayoutResources
            .associateBy { resource ->
                val identifiedObjectId = resource.getObjectPropertyByPredicateOrNull(
                    CimClasses.DiagramObject.IdentifiedObject
                )?.anotherResourceId

                identifiedObjectId.toString()
            }

        return getFrequencyToEquipmentIdsMap(scheme)
            .mapKeys { (frequency, equipmentIds) ->
                RdfResourceBuilder(UUID.randomUUID().toString(), CimClasses.BaseFrequency)
                    .addDataProperty(CimClasses.BaseFrequency.frequency, frequency)
                    .let { resource ->
                        equipmentIds.forEach { equipmentId ->
                            identifiedObjectIdToDiagramLayoutResourceMap[equipmentId]?.let { diagramObject ->
                                resource.addObjectProperty(CimClasses.IdentifiedObject.DiagramObjects, diagramObject)
                            }
                        }

                        resource
                    }
                    .build()
            }
            .keys
    }

    private fun getFrequencyToEquipmentIdsMap(scheme: RawSchemeDto): Map<Double, List<String>> {
        val frequencyToEquipmentIdsMap = mutableMapOf<Double, MutableList<String>>()

        scheme.nodes.values.forEach { equipment ->
            if (equipmentsWithFrequency.contains(equipment.libEquipmentId)) {
                val frequency = equipment.getFieldDoubleValue(FieldLibId.FREQUENCY)
                frequencyToEquipmentIdsMap.computeIfAbsent(frequency) { mutableListOf() }.add(equipment.id)
            }
        }

        return frequencyToEquipmentIdsMap
    }
}
