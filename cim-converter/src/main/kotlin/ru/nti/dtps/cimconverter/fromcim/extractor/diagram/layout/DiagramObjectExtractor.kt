package ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout // package ru.nti.dtps.cimconverter.fromcim.extractor.diagram

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.convertDegreesToHour
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.*
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses

object DiagramObjectExtractor {

    private val cimClass = CimClasses.DiagramObject

    fun extract(
        repository: RdfRepository,
        diagramObjectIdToPointsMap: Map<String, List<DiagramObjectPoint>>
    ): Map<String, DiagramObject> = create(
        repository.selectAllVarsFromTriples(
            cimClass,
            CimClasses.DiagramObject.IdentifiedObject,
            CimClasses.DiagramObject.rotation
        ),
        diagramObjectIdToPointsMap
    ).associateBy { it.relatedObjectId }

    private fun create(
        queryResult: TupleQueryResult,
        diagramObjectIdToPointsMap: Map<String, List<DiagramObjectPoint>>
    ): List<DiagramObject> {
        return queryResult.mapNotNull { bindingSet ->
            val id = bindingSet.extractIdentifiedObjectId()
            val relatedObjectId = bindingSet.extractObjectReferenceOrNull(CimClasses.DiagramObject.IdentifiedObject)
            val hour = bindingSet
                .extractIntValueOrNull(CimClasses.DiagramObject.rotation)
                ?.let(::convertDegreesToHour) ?: 0

            val points = diagramObjectIdToPointsMap[id]
            if (relatedObjectId != null && points != null) {
                DiagramObject(id, relatedObjectId, hour, points.sortedBy { it.sequenceNumber })
            } else {
                null
            }
        }
    }
}
