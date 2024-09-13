package ru.nti.dtps.cimconverter.fromcim.extractor.voltage.basevoltage

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.*
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.equipment.meta.info.manager.VoltageMetaInfoManager

object BaseVoltageExtractor {

    fun extract(repository: RdfRepository): Map<String, BaseVoltage> = create(
        repository.selectAllVarsFromTriples(
            CimClasses.BaseVoltage,
            CimClasses.BaseVoltage.nominalVoltage
        )
    ).associateBy { it.id }

    private fun create(queryResult: TupleQueryResult): List<BaseVoltage> {
        return queryResult.map { bindingSet ->
            val id = bindingSet.extractIdentifiedObjectId()
            val nominalVoltageInKilovolts = bindingSet.extractDoubleValueOrNull(CimClasses.BaseVoltage.nominalVoltage)
            BaseVoltage(
                id,
                voltageLevelLib = nominalVoltageInKilovolts?.let {
                    VoltageMetaInfoManager.findNearestVoltageLevelByVoltageInKilovolts(nominalVoltageInKilovolts)
                }
            )
        }
    }
}
