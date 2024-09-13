package ru.nti.dtps.cimconverter.fromcim.extractor.lines

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIdentifiedObjectId
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIdentifiedObjectNameOrNull
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.RawSchemeDto

object LinesExtractor {

    fun extract(repository: RdfRepository): Map<String, RawSchemeDto.TransmissionLineDto> = create(
        repository.selectAllVarsFromTriples(
            CimClasses.Line,
            CimClasses.IdentifiedObject.name
        )
    ).associateBy(RawSchemeDto.TransmissionLineDto::id)

    private fun create(queryResult: TupleQueryResult): List<RawSchemeDto.TransmissionLineDto> {
        return queryResult.mapIndexed { index, bindingSet ->
            val id = bindingSet.extractIdentifiedObjectId()
            val name = bindingSet.extractIdentifiedObjectNameOrNull()
            RawSchemeDto.TransmissionLineDto(id, name ?: "ЛЭП ${index + 1}")
        }
    }
}
