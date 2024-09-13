package ru.nti.dtps.cimconverter.fromcim.extractor.substation

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIdentifiedObjectId
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIdentifiedObjectNameOrNull
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.RawSchemeDto

object SubstationExtractor {

    fun extract(repository: RdfRepository): Map<String, RawSchemeDto.SubstationDto> = create(
        repository.selectAllVarsFromTriples(
            subject = CimClasses.Substation,
            CimClasses.IdentifiedObject.name
        )
    ).associateBy(RawSchemeDto.SubstationDto::id)

    private fun create(
        queryResult: TupleQueryResult
    ): List<RawSchemeDto.SubstationDto> = queryResult.mapIndexed { index, bindingSet ->
        val id = bindingSet.extractIdentifiedObjectId()
        val name = bindingSet.extractIdentifiedObjectNameOrNull()
        RawSchemeDto.SubstationDto(id, name ?: "Подстанция ${index + 1}")
    }
}
