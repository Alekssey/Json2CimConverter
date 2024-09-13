package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.machine.synchronous

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractDoubleValueOrNull
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractStringValueOrNull
import ru.nti.dtps.cimconverter.rdf.schema.Cim302Classes
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses

object SynchronousMachineEquivalentCircuitExtractor {
    private val cimClass = Cim302Classes.SynchronousMachineEquivalentCircuit

    fun extract(
        repository: RdfRepository
    ): Map<String, SynchronousMachineEquivalentCircuit> = create(
        repository.selectAllVarsFromTriples(
            cimClass,
            CimClasses.IdentifiedObject.name,
            Cim302Classes.SynchronousMachineDynamics.SynchronousMachine,
            Cim302Classes.SynchronousMachineEquivalentCircuit.xaq,
            Cim302Classes.SynchronousMachineEquivalentCircuit.xad,
            Cim302Classes.SynchronousMachineEquivalentCircuit.xf1d,
            Cim302Classes.SynchronousMachineEquivalentCircuit.rfd,
            Cim302Classes.SynchronousMachineEquivalentCircuit.r1q,
            Cim302Classes.SynchronousMachineEquivalentCircuit.r1d,
            Cim302Classes.SynchronousMachineEquivalentCircuit.r2q,
            Cim302Classes.SynchronousMachineEquivalentCircuit.x1d,
            Cim302Classes.SynchronousMachineEquivalentCircuit.x2q,
            Cim302Classes.SynchronousMachineEquivalentCircuit.xfd

        )
    )

    private fun create(
        queryResult: TupleQueryResult
    ): Map<String, SynchronousMachineEquivalentCircuit> = queryResult.mapNotNull { bindingSet ->
        bindingSet.extractStringValueOrNull(
            Cim302Classes.SynchronousMachineDynamics.SynchronousMachine
        )?.let { synchronousMachineId ->
            SynchronousMachineEquivalentCircuit(
                synchronousMachineId = synchronousMachineId,
                statorRotorMutualInductanceQAxis = bindingSet.extractDoubleValueOrNull(Cim302Classes.SynchronousMachineEquivalentCircuit.xaq),
                statorRotorMutualInductanceDAxis = bindingSet.extractDoubleValueOrNull(Cim302Classes.SynchronousMachineEquivalentCircuit.xad),
                firstDamperWindingLeakageReactanceQAxis = bindingSet.extractDoubleValueOrNull(Cim302Classes.SynchronousMachineEquivalentCircuit.xf1d),
                drivingWindingResistance = bindingSet.extractDoubleValueOrNull(Cim302Classes.SynchronousMachineEquivalentCircuit.rfd),
                firstDamperWindingResistanceQAxis = bindingSet.extractDoubleValueOrNull(Cim302Classes.SynchronousMachineEquivalentCircuit.r1q),
                damperWindingResistanceDAxis = bindingSet.extractDoubleValueOrNull(Cim302Classes.SynchronousMachineEquivalentCircuit.r1d),
                secondDamperWindingResistanceQAxis = bindingSet.extractDoubleValueOrNull(Cim302Classes.SynchronousMachineEquivalentCircuit.r2q),
                damperWindingLeakageReactanceDAxis = bindingSet.extractDoubleValueOrNull(Cim302Classes.SynchronousMachineEquivalentCircuit.x1d),
                secondDamperWindingLeakageReactanceQAxis = bindingSet.extractDoubleValueOrNull(Cim302Classes.SynchronousMachineEquivalentCircuit.x2q),
                damperWindingDrivingMutualIndactanceDAxis = bindingSet.extractDoubleValueOrNull(Cim302Classes.SynchronousMachineEquivalentCircuit.xfd)
            )
        }
    }.associateBy(SynchronousMachineEquivalentCircuit::synchronousMachineId)
}
