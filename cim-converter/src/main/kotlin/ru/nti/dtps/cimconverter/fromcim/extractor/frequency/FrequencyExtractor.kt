package ru.nti.dtps.cimconverter.fromcim.extractor.frequency

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.*
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractObjectReferenceOrNull
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses

object FrequencyExtractor {

    fun extract(repository: RdfRepository): Map<String, Double> = retrieveEquipmentIdToFrequencyMap(
        retrieveDiagramObjectIdToFrequencyMap(
            repository.selectAllVarsFromTriples(
                subject = CimClasses.BaseFrequency,
                CimClasses.BaseFrequency.frequency,
                CimClasses.IdentifiedObject.DiagramObjects
            )
        ),
        repository.selectAllVarsFromTriples(
            subject = CimClasses.DiagramObject,
            CimClasses.DiagramObject.IdentifiedObject
        )
    )

    private fun retrieveDiagramObjectIdToFrequencyMap(
        queryResult: TupleQueryResult
    ): Map<String, Double> {
        val diagramObjectIdToFrequencyMap = mutableMapOf<String, Double>()

        queryResult.forEach { bindingSet ->
            val frequency = bindingSet.extractDoubleValueOrNull(CimClasses.BaseFrequency.frequency)
            val diagramObjectId = bindingSet.extractObjectReferenceOrNull(CimClasses.IdentifiedObject.DiagramObjects)
            if (frequency != null && diagramObjectId != null) {
                diagramObjectIdToFrequencyMap[diagramObjectId] = frequency
            }
        }

        return diagramObjectIdToFrequencyMap
    }

    private fun retrieveEquipmentIdToFrequencyMap(
        diagramObjectIdToFrequencyMap: Map<String, Double>,
        queryResult: TupleQueryResult
    ): Map<String, Double> {
        val equipmentIdToFrequencyMap = mutableMapOf<String, Double>()

        queryResult.forEach { bindingSet ->
            val id = bindingSet.extractIdentifiedObjectId()
            val resource = bindingSet.extractObjectReferenceOrNull(CimClasses.DiagramObject.IdentifiedObject)
            if (resource != null) {
                diagramObjectIdToFrequencyMap[id]?.let { frequency ->
                    equipmentIdToFrequencyMap[resource] = frequency
                }
            }
        }

        return equipmentIdToFrequencyMap
    }
}
