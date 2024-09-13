package ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.voltages

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.service.graphsearch.BreadthFirstSearch
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getName
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getNameOrId
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getVoltageInKilovolts
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.isThereVoltageLevel
import ru.nti.dpts.schememanagerback.scheme.service.ops.findNearestVoltageLevelByVoltageInKilovolts
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.VoltageValidationError
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.VoltageValidationException
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId

@Component
class VoltagesValidatorWithSources {

    private val sourceLibIds = setOf(
        EquipmentLibId.POWER_SYSTEM_EQUIVALENT,
        EquipmentLibId.SYNCHRONOUS_GENERATOR,
        EquipmentLibId.SOURCE_OF_ELECTROMOTIVE_FORCE_DC
    )

    fun validate(scheme: SchemeDomain): Either<VoltageValidationError, Unit> {
        Either.catch {
            BreadthFirstSearch(scheme) { node -> sourceLibIds.contains(node.libEquipmentId) }.also { bfs ->
                scheme.nodes.values
                    .first { sourceLibIds.contains(it.libEquipmentId) }
                    .also { source ->
                        bfs.searchFromNode(source) { sibling, _, _ ->
                            if (sourceLibIds.contains(sibling.libEquipmentId)) {
                                validateVoltagesForTwoSourceInScheme(
                                    source,
                                    sibling
                                )
                            } else if (sibling.isThereVoltageLevel()) {
                                validateVoltagesForSourceAndOtherEquipment(
                                    source,
                                    sibling
                                )
                            }
                        }
                    }
            }
        }.mapLeft {
            if (it is VoltageValidationException) {
                return it.error.left()
            }
        }
        return Unit.right()
    }

    private fun validateVoltagesForTwoSourceInScheme(
        source: EquipmentNodeDomain,
        siblingSource: EquipmentNodeDomain
    ) {
        val voltageValueInKilovolts = source.getVoltageInKilovolts()
        val sourceVoltageLevel = findNearestVoltageLevelByVoltageInKilovolts(voltageValueInKilovolts)

        val siblingVoltageValueInKilovolts = siblingSource.getVoltageInKilovolts()
        val siblingVoltageLevel = findNearestVoltageLevelByVoltageInKilovolts(siblingVoltageValueInKilovolts)

        if (sourceVoltageLevel != siblingVoltageLevel) {
            throw VoltageValidationException(
                VoltageValidationError.ConnectedNodesHaveDifferentVoltageLevels(
                    source.id,
                    source.getName(),
                    siblingSource.getName(),
                    voltageValueInKilovolts,
                    siblingVoltageValueInKilovolts
                ),
                null
            )
        }
    }

    private fun validateVoltagesForSourceAndOtherEquipment(
        source: EquipmentNodeDomain,
        equipmentNode: EquipmentNodeDomain
    ) {
        val voltageValueInKilovolts = source.getVoltageInKilovolts()
        val equipmentSecondVoltageInKilovolts = equipmentNode.getVoltageInKilovolts()

        val sourceVoltageLevel = findNearestVoltageLevelByVoltageInKilovolts(voltageValueInKilovolts)
        val equipmentNodeVoltageLevel = findNearestVoltageLevelByVoltageInKilovolts(equipmentSecondVoltageInKilovolts)
        if (sourceVoltageLevel.voltageInKilovolts != equipmentNodeVoltageLevel.voltageInKilovolts) {
            throw VoltageValidationException(
                VoltageValidationError.ConnectedNodesHaveDifferentVoltageLevels(
                    source.id,
                    source.getName(),
                    equipmentNode.getNameOrId(),
                    voltageValueInKilovolts,
                    equipmentSecondVoltageInKilovolts
                ),
                null
            )
        }
    }
}
