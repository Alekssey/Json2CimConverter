package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.asyncronousmotor

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractDoubleValueOrNull
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIdentifiedObjectId
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractObjectReferenceOrNull
import ru.nti.dtps.cimconverter.rdf.schema.Cim302Classes

object AsynchronousMachineEquivalentCircuitExtractor {

    private val cimClass = Cim302Classes.AsynchronousMachineEquivalentCircuit

    fun extract(repository: RdfRepository): Map<String, AsynchronousMachineEquivalentCircuit> = create(
        repository.selectAllVarsFromTriples(
            cimClass,
            Cim302Classes.AsynchronousMachineDynamics.AsynchronousMachine,
            Cim302Classes.RotatingMachineDynamics.inertia,
            Cim302Classes.RotatingMachineDynamics.damping,
            Cim302Classes.RotatingMachineDynamics.statorResistance,
            Cim302Classes.AsynchronousMachineEquivalentCircuit.xm
        )
    )

    fun create(
        queryResult: TupleQueryResult
    ): Map<String, AsynchronousMachineEquivalentCircuit> {
        val asynchronousMotorIdToAsynchronousMachineEqCircuitMap = mutableMapOf<String, AsynchronousMachineEquivalentCircuit>()

        queryResult.forEach { bindingSet ->
            val id = bindingSet.extractIdentifiedObjectId()
            val asyncronousMotorId = bindingSet.extractObjectReferenceOrNull(
                Cim302Classes.AsynchronousMachineDynamics.AsynchronousMachine
            )

            asyncronousMotorId?.let {
                asynchronousMotorIdToAsynchronousMachineEqCircuitMap[asyncronousMotorId] = AsynchronousMachineEquivalentCircuit(
                    id,
                    inertia = bindingSet.extractDoubleValueOrNull(Cim302Classes.RotatingMachineDynamics.inertia),
                    damping = bindingSet.extractDoubleValueOrNull(Cim302Classes.RotatingMachineDynamics.damping),
                    statorResistance = bindingSet.extractDoubleValueOrNull(Cim302Classes.RotatingMachineDynamics.statorResistance),
                    xm = bindingSet.extractDoubleValueOrNull(Cim302Classes.AsynchronousMachineEquivalentCircuit.xm)
                )
            }
        }

        return asynchronousMotorIdToAsynchronousMachineEqCircuitMap
    }
}
