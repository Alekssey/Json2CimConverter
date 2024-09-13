package ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.machine.synchronous

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractDoubleValueOrNull
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractStringValueOrNull
import ru.nti.dtps.cimconverter.rdf.schema.Cim302Classes
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses

object ExcitationSystemDynamicsExtractor {
    private val cimClass = Cim302Classes.ExcREXS

    fun extract(
        repository: RdfRepository
    ): Map<String, ExcitationSystemDynamics> = create(
        repository.selectAllVarsFromTriples(
            cimClass,
            CimClasses.IdentifiedObject.name,
            Cim302Classes.ExcitationSystemDynamics.SynchronousMachineDynamics,
            Cim302Classes.ExcREXS.kvp,
            Cim302Classes.ExcREXS.tb1,
            Cim302Classes.ExcREXS.kd,
            Cim302Classes.ExcREXS.ta,
            Cim302Classes.ExcREXS.tf,
            Cim302Classes.ExcREXS.te,
            Cim302Classes.ExcREXS.kf,
            Cim302Classes.ExcREXS.vrmax,
            Cim302Classes.ExcREXS.vrmin
        )
    )

    private fun create(
        queryResult: TupleQueryResult
    ): Map<String, ExcitationSystemDynamics> = queryResult.mapNotNull { bindingSet ->
        bindingSet.extractStringValueOrNull(
            Cim302Classes.ExcitationSystemDynamics.SynchronousMachineDynamics
        )?.let { synchronousMachineDynamicsId ->
            ExcitationSystemDynamics(
                synchronousMachineDynamicsId = synchronousMachineDynamicsId,
                channelGainByVoltageDeviationCoefficient = bindingSet.extractDoubleValueOrNull(Cim302Classes.ExcREXS.kvp),
                channelIntegrationTimeConstantForVoltageDeviationCoefficient = bindingSet.extractDoubleValueOrNull(Cim302Classes.ExcREXS.tb1),
                stabilizationChannelGainBasedOnVoltageChangeRate = bindingSet.extractDoubleValueOrNull(Cim302Classes.ExcREXS.kd),
                stabilizationChannelTimeConstantForVoltageChangeRate = bindingSet.extractDoubleValueOrNull(Cim302Classes.ExcREXS.ta),
                stabilizationChannelTimeConstantOnDrivingCurrentChangeRate = bindingSet.extractDoubleValueOrNull(Cim302Classes.ExcREXS.tf),
                drivingSystemTimeConstant = bindingSet.extractDoubleValueOrNull(Cim302Classes.ExcREXS.te),
                drivingSystemGain = bindingSet.extractDoubleValueOrNull(Cim302Classes.ExcREXS.kf),
                drivingSystemUpperLimit = bindingSet.extractDoubleValueOrNull(Cim302Classes.ExcREXS.vrmax),
                drivingSystemLowerLimit = bindingSet.extractDoubleValueOrNull(Cim302Classes.ExcREXS.vrmin)
            )
        }
    }.associateBy(ExcitationSystemDynamics::synchronousMachineDynamicsId)
}
