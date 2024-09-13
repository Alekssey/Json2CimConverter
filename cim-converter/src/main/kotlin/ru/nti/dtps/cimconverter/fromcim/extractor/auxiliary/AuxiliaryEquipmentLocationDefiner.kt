package ru.nti.dtps.cimconverter.fromcim.extractor.auxiliary

import ru.nti.dtps.cimconverter.fromcim.extractor.terminal.Terminal
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import java.util.UUID

object AuxiliaryEquipmentLocationDefiner {

    fun createAuxiliaryEquipmentTerminals(
        repository: RdfRepository,
        connectivityNodeIdToTerminalsMap: Map<String, List<Terminal>>
    ): Map<String, List<Terminal>> {
        val auxiliaryEquipmentIdToTerminalIdMap: Map<String, String> = AuxiliaryEquipmentExtractor
            .extractAuxiliaryEquipmentIdToTerminalIdMap(repository)

        return connectivityNodeIdToTerminalsMap.mapValues { (_, terminals) ->
            terminals + auxiliaryEquipmentIdToTerminalIdMap
                .entries
                .filter { (_, terminalId) -> terminals.any { it.id == terminalId } }
                .map { (auxiliaryEquipmentId, _) ->
                    val phaseCode = if (
                        terminals
                            .mapNotNull(Terminal::phaseCode)
                            .any(CimClasses.PhaseCode.singlePhaseCodes::contains)
                    ) {
                        CimClasses.PhaseCode.A
                    } else {
                        CimClasses.PhaseCode.ABC
                    }

                    Terminal(
                        id = UUID.randomUUID().toString(),
                        equipmentId = auxiliaryEquipmentId,
                        sequenceNumber = 1,
                        phaseCode = phaseCode
                    )
                }
        }
    }
}
