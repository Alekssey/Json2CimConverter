package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.machine.synchronous

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractDoubleValueOrNull
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractStringValueOrNull
import ru.nti.dtps.cimconverter.rdf.schema.Cim302Classes
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses

object SynchronousMachineTimeConstantReactanceExtractor {
    private val cimClass = Cim302Classes.SynchronousMachineTimeConstantReactance

    fun extract(
        repository: RdfRepository
    ): Map<String, SynchronousMachineTimeConstantReactance> = create(
        repository.selectAllVarsFromTriples(
            cimClass,
            CimClasses.IdentifiedObject.name,
            Cim302Classes.SynchronousMachineDynamics.SynchronousMachine,
            Cim302Classes.SynchronousMachineTimeConstantReactance.xDirectSync
        )
    )

    private fun create(
        queryResult: TupleQueryResult
    ): Map<String, SynchronousMachineTimeConstantReactance> = queryResult.mapNotNull { bindingSet ->
        bindingSet.extractStringValueOrNull(
            Cim302Classes.SynchronousMachineDynamics.SynchronousMachine
        )?.let { synchronousMachineId ->
            SynchronousMachineTimeConstantReactance(
                synchronousMachineId = synchronousMachineId,
                drivingWindingLeakageReactance = bindingSet.extractDoubleValueOrNull(Cim302Classes.SynchronousMachineEquivalentCircuit.xaq)
            )
        }
    }.associateBy(SynchronousMachineTimeConstantReactance::synchronousMachineId)
}
