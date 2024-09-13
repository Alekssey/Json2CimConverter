package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.machine.synchronous

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractDoubleValueOrNull
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractStringValueOrNull
import ru.nti.dtps.cimconverter.rdf.schema.Cim302Classes
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.DtpsClasses

object TurbineGovernorDynamicsExtractor {
    private val cimClass = Cim302Classes.GovCT1

    fun extract(
        repository: RdfRepository
    ): Map<String, TurbineGovernorDynamics> = create(
        repository.selectAllVarsFromTriples(
            cimClass,
            CimClasses.IdentifiedObject.name,
            Cim302Classes.TurbineGovernorDynamics.SynchronousMachineDynamics,
            Cim302Classes.GovCT1.tb,
            Cim302Classes.GovCT1.ldref,
            DtpsClasses.GovCT1.initialPosition,
            Cim302Classes.GovCT1.ka,
            Cim302Classes.GovCT1.rdown,
            Cim302Classes.GovCT1.rup,
            DtpsClasses.GovCT1.frequencySetPoint,
            Cim302Classes.GovCT1.mwbase,
            Cim302Classes.GovCT1.kigov
        )
    )

    private fun create(
        queryResult: TupleQueryResult
    ): Map<String, TurbineGovernorDynamics> = queryResult.mapNotNull { bindingSet ->
        bindingSet.extractStringValueOrNull(
            Cim302Classes.TurbineGovernorDynamics.SynchronousMachineDynamics
        )?.let { synchronousMachineDynamicsId ->
            TurbineGovernorDynamics(
                synchronousMachineDynamicsId = synchronousMachineDynamicsId,
                turbineTimeConstant = bindingSet.extractDoubleValueOrNull(Cim302Classes.GovCT1.tb),
                turbinePowerPu = bindingSet.extractDoubleValueOrNull(Cim302Classes.GovCT1.ldref),
                turbineInitialState = bindingSet.extractDoubleValueOrNull(DtpsClasses.GovCT1.initialPosition),
                speedRelayGain = bindingSet.extractDoubleValueOrNull(Cim302Classes.GovCT1.ka),
                flapStateLowerLimit = bindingSet.extractDoubleValueOrNull(Cim302Classes.GovCT1.rdown),
                flapStateUpperLimit = bindingSet.extractDoubleValueOrNull(Cim302Classes.GovCT1.rup),
                frequencySetPoint = bindingSet.extractDoubleValueOrNull(DtpsClasses.GovCT1.frequencySetPoint),
                powerSetPoint = bindingSet.extractDoubleValueOrNull(Cim302Classes.GovCT1.mwbase),
                integrationOfPowerRegulationConstant = bindingSet.extractDoubleValueOrNull(Cim302Classes.GovCT1.kigov)
            )
        }
    }.associateBy(TurbineGovernorDynamics::synchronousMachineDynamicsId)
}
