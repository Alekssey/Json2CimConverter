package ru.nti.dtps.cimconverter.fromcim.extractor.voltage.voltagelevel

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.basevoltage.BaseVoltage
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIdentifiedObjectId
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractObjectReferenceOrNull
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.RawSchemeDto

object VoltageLevelExtractor {

    fun extract(
        repository: RdfRepository,
        substations: Map<String, RawSchemeDto.SubstationDto>,
        baseVoltages: Map<String, BaseVoltage>
    ): Map<String, VoltageLevel> = create(
        repository.selectAllVarsFromTriples(
            CimClasses.VoltageLevel,
            CimClasses.VoltageLevel.BaseVoltage,
            CimClasses.VoltageLevel.Substation
        ),
        substations,
        baseVoltages
    ).associateBy { it.id }

    private fun create(
        queryResult: TupleQueryResult,
        substations: Map<String, RawSchemeDto.SubstationDto>,
        baseVoltages: Map<String, BaseVoltage>
    ): List<VoltageLevel> {
        return queryResult.map { bindingSet ->
            VoltageLevel(
                id = bindingSet.extractIdentifiedObjectId(),
                substation = bindingSet
                    .extractObjectReferenceOrNull(CimClasses.VoltageLevel.Substation)
                    ?.let(substations::get),
                baseVoltage = bindingSet
                    .extractObjectReferenceOrNull(CimClasses.VoltageLevel.BaseVoltage)
                    ?.let(baseVoltages::get)
            )
        }
    }
}
