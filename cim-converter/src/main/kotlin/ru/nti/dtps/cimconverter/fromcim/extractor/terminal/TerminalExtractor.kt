package ru.nti.dtps.cimconverter.fromcim.extractor.terminal

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.exception.CimDataException
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.*
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses

object TerminalExtractor {

    private val cimClass = CimClasses.Terminal

    fun createConnectivityNodeIdToTerminalsMap(
        repository: RdfRepository
    ): Map<String, List<Terminal>> = create(
        getTerminalIdToPowerTransformerEndMap(repository),
        repository.selectAllVarsFromTriples(
            cimClass,
            CimClasses.ACDCTerminal.sequenceNumber,
            CimClasses.Terminal.ConductingEquipment,
            CimClasses.Terminal.TopologicalNode,
            CimClasses.Terminal.ConnectivityNode,
            CimClasses.Terminal.phases
        ),
        getTopologicalNodeIdToVoltageMap(repository)
    )

    private fun create(
        terminalIdToPowerTransformerEndMap: Map<String, PowerTransformerEnd>,
        queryResult: TupleQueryResult,
        topologicalNodeIdToVoltageMap: Map<String, Terminal.Voltage>
    ): Map<String, List<Terminal>> {
        val equipmentIdToTerminalsMap = mutableMapOf<String, MutableList<Terminal>>()

        queryResult.forEach { bindingSet ->
            val equipmentId = bindingSet.extractObjectReferenceOrNull(CimClasses.Terminal.ConductingEquipment)
            val connectivityNodeId = bindingSet.extractObjectReferenceOrNull(CimClasses.Terminal.ConnectivityNode)
            val topologicalNodeId = bindingSet.extractObjectReferenceOrNull(CimClasses.Terminal.TopologicalNode)
            val phaseCode = bindingSet.extractObjectReferenceOrNull(CimClasses.Terminal.phases)?.let(CimClasses.PhaseCode::parseOrNull)

            if (equipmentId != null && connectivityNodeId != null) {
                val terminal = Terminal(
                    id = bindingSet.extractIdentifiedObjectId(),
                    equipmentId = equipmentId,
                    sequenceNumber = bindingSet.extractIntValueOrNull(CimClasses.ACDCTerminal.sequenceNumber)?.let {
                        if (it == 0) null else it
                    },
                    connectivityNodeId = connectivityNodeId,
                    voltage = topologicalNodeIdToVoltageMap[topologicalNodeId],
                    phaseCode = phaseCode
                )

                equipmentIdToTerminalsMap.computeIfAbsent(equipmentId) { mutableListOf() } += terminal
            }
        }

        return equipmentIdToTerminalsMap.values.flatten().groupBy { it.connectivityNodeId!! }
    }

    private fun getTerminalIdToPowerTransformerEndMap(
        repository: RdfRepository
    ): Map<String, PowerTransformerEnd> {
        val queryResult = repository.selectAllVarsFromTriples(
            CimClasses.PowerTransformerEnd,
            CimClasses.TransformerEnd.endNumber,
            CimClasses.TransformerEnd.Terminal
        )

        return queryResult.mapNotNull { bindingSet ->
            val id = bindingSet.extractIdentifiedObjectId()
            val endNumber = bindingSet.extractIntValueOrNull(CimClasses.TransformerEnd.endNumber)?.also {
                if (it == 0) throw CimDataException("End number of power transformer end $id equals to 0")
            }
            val terminalId = bindingSet.extractObjectReferenceOrNull(CimClasses.TransformerEnd.Terminal)
            if (endNumber != null && terminalId != null) {
                PowerTransformerEnd(id, endNumber, terminalId)
            } else {
                null
            }
        }.associateBy(PowerTransformerEnd::terminalId)
    }

    private fun getTopologicalNodeIdToVoltageMap(repository: RdfRepository): Map<String, Terminal.Voltage> {
        val topologicalNodeIdToVoltageMap = mutableMapOf<String, Terminal.Voltage>()

        val queryResult = repository.selectAllVarsFromTriples(
            CimClasses.SvVoltage,
            CimClasses.SvVoltage.TopologicalNode,
            CimClasses.SvVoltage.v,
            CimClasses.SvVoltage.angle
        )
        queryResult.mapNotNull { bindingSet ->
            val tnId = bindingSet.extractObjectReferenceOrNull(CimClasses.SvVoltage.TopologicalNode)
            val voltage = bindingSet.extractDoubleValueOrNull(CimClasses.SvVoltage.v)
            val angle = bindingSet.extractDoubleValueOrNull(CimClasses.SvVoltage.angle)
            if (tnId != null && voltage != null && angle != null) {
                topologicalNodeIdToVoltageMap[tnId] = Terminal.Voltage(
                    magnitudeInKilovolts = voltage,
                    angleInDegree = angle
                )
            }
        }
        return topologicalNodeIdToVoltageMap
    }

    class PowerTransformerEnd(
        val id: String,
        val endNumber: Int,
        val terminalId: String
    )
}
