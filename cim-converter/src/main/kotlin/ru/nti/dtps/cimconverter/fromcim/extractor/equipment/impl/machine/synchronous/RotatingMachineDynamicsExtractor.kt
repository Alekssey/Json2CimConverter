package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.machine.synchronous

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractDoubleValueOrNull
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIdentifiedObjectId
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractStringValueOrNull
import ru.nti.dtps.cimconverter.rdf.schema.Cim302Classes
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses

object RotatingMachineDynamicsExtractor {
    private val cimClass = Cim302Classes.RotatingMachineDynamics

    fun extract(
        repository: RdfRepository
    ): Map<String, RotatingMachineDynamics> = create(
        repository.selectAllVarsFromTriples(
            cimClass,
            CimClasses.IdentifiedObject.name,
            Cim302Classes.SynchronousMachineDynamics.SynchronousMachine,
            Cim302Classes.RotatingMachineDynamics.inertia,
            Cim302Classes.RotatingMachineDynamics.statorLeakageReactance,
            Cim302Classes.RotatingMachineDynamics.damping,
            Cim302Classes.RotatingMachineDynamics.statorResistance
        )
    )

    private fun create(
        queryResult: TupleQueryResult
    ): Map<String, RotatingMachineDynamics> = queryResult.mapNotNull { bindingSet ->
        bindingSet.extractStringValueOrNull(
            Cim302Classes.SynchronousMachineDynamics.SynchronousMachine
        )?.let { synchronousMachineId ->
            RotatingMachineDynamics(
                id = bindingSet.extractIdentifiedObjectId(),
                synchronousMachineId = synchronousMachineId,
                inertiaConstant = bindingSet.extractDoubleValueOrNull(Cim302Classes.RotatingMachineDynamics.inertia),
                statorLeakageReactance = bindingSet.extractDoubleValueOrNull(Cim302Classes.RotatingMachineDynamics.statorLeakageReactance),
                viscousFrictionCoefficient = bindingSet.extractDoubleValueOrNull(Cim302Classes.RotatingMachineDynamics.damping),
                statorActiveResistance = bindingSet.extractDoubleValueOrNull(Cim302Classes.RotatingMachineDynamics.statorResistance)
            )
        }
    }.associateBy(RotatingMachineDynamics::synchronousMachineId)
}
