package ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout // package ru.nti.dtps.cimconverter.fromcim.extractor.diagram

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractDoubleValueOrNull
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIntValueOrNull
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractObjectReferenceOrNull
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.XyDto

object DiagramObjectPointExtractor {

    private val cimClass = CimClasses.DiagramObjectPoint

    fun extract(repository: RdfRepository): Map<String, List<DiagramObjectPoint>> = create(
        repository.selectAllVarsFromTriples(
            cimClass,
            CimClasses.DiagramObjectPoint.DiagramObject,
            CimClasses.DiagramObjectPoint.sequenceNumber,
            CimClasses.DiagramObjectPoint.xPosition,
            CimClasses.DiagramObjectPoint.yPosition
        )
    )

    private fun create(
        queryResult: TupleQueryResult
    ): Map<String, List<DiagramObjectPoint>> {
        val diagramObjectIdToPointsMap = mutableMapOf<String, MutableList<DiagramObjectPoint>>()

        queryResult.mapNotNull { bindingSet ->
            val diagramObjectId = bindingSet.extractObjectReferenceOrNull(CimClasses.DiagramObjectPoint.DiagramObject)
            val sequenceNumber = bindingSet.extractIntValueOrNull(CimClasses.DiagramObjectPoint.sequenceNumber)
            val x = bindingSet.extractDoubleValueOrNull(CimClasses.DiagramObjectPoint.xPosition)
            val y = bindingSet.extractDoubleValueOrNull(CimClasses.DiagramObjectPoint.yPosition)

            if (diagramObjectId != null && x != null && y != null) {
                diagramObjectIdToPointsMap.computeIfAbsent(diagramObjectId) { mutableListOf() } += DiagramObjectPoint(
                    diagramObjectId,
                    sequenceNumber,
                    XyDto(x, y)
                )
            }
        }

        return diagramObjectIdToPointsMap
    }
}
