package ru.nti.dtps.cimconverter.fromcim.extractor.auxiliary

import org.eclipse.rdf4j.query.TupleQueryResult
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.*
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.CimField

object AuxiliaryEquipmentExtractor {

    private val auxiliaryEquipmentCimClassToCimTerminalObjectReference = listOf(
        CimClasses.EquipmentFault to CimClasses.EquipmentFault.Terminal,
        CimClasses.PotentialTransformer to CimClasses.AuxiliaryEquipment.Terminal
    )

    fun extractAuxiliaryEquipmentIdToTerminalIdMap(
        repository: RdfRepository
    ): Map<String, String> {
        val auxiliaryEquipmentIdToTerminalIdMap = mutableMapOf<String, String>()
        auxiliaryEquipmentCimClassToCimTerminalObjectReference
            .forEach { (auxiliaryEquipmentCimClass, cimTerminalObjectReference) ->
                register(
                    repository.selectAllVarsFromTriples(
                        auxiliaryEquipmentCimClass,
                        cimTerminalObjectReference
                    ),
                    cimTerminalObjectReference
                ) { auxiliaryEquipmentId, terminalId ->
                    auxiliaryEquipmentIdToTerminalIdMap[auxiliaryEquipmentId] = terminalId
                }
            }

        return auxiliaryEquipmentIdToTerminalIdMap
    }

    private fun register(
        queryResult: TupleQueryResult,
        terminalObjectReference: CimField,
        register: (auxiliaryEquipmentId: String, terminalId: String) -> Unit
    ) {
        queryResult.forEach { bindingSet ->
            val auxiliaryEquipmentId = bindingSet.extractIdentifiedObjectId()
            val auxiliaryEquipmentTerminal = bindingSet.extractObjectReferenceOrNull(terminalObjectReference)

            auxiliaryEquipmentTerminal?.let { register(auxiliaryEquipmentId, it) }
        }
    }
}
