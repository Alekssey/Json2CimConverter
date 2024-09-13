package ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.voltages

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentLinkDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.PortDto
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.service.graphsearch.BreadthFirstSearch
import ru.nti.dpts.schememanagerback.scheme.service.graphsearch.DoOnSiblings
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.*
import ru.nti.dpts.schememanagerback.scheme.service.ops.findNearestVoltageLevelByVoltageInKilovolts
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.VoltageValidationError
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.VoltageValidationException
import ru.nti.dtps.equipment.meta.info.extension.isPowerTransformer

@Component
class VoltagesValidatorWithPowerTransformer {

    fun validate(scheme: SchemeDomain): Either<VoltageValidationError, Unit> {
        Either.catch {
            BreadthFirstSearch(scheme) { node -> node.libEquipmentId.isPowerTransformer() }.also { bfs ->
                scheme.nodes.values
                    .filter { it.libEquipmentId.isPowerTransformer() }
                    .forEach { powerTransformer ->
                        powerTransformer.ports.forEach { port ->
                            val functionForCheckingVoltages = getFunctionForCheckingVoltages(
                                powerTransformer,
                                port
                            )
                            bfs.searchFromNodeAndSpecialPortAndDoOnSiblings(
                                powerTransformer,
                                port,
                                functionForCheckingVoltages
                            )
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

    private fun getFunctionForCheckingVoltages(
        powerTransformer: EquipmentNodeDomain,
        powerTransformerPort: PortDto
    ): DoOnSiblings {
        return { sibling: EquipmentNodeDomain, _: EquipmentLinkDomain, siblingPort: PortDto ->

            if (sibling.libEquipmentId.isPowerTransformer()) {
                validateVoltagesForTwoPowerTransformerInScheme(
                    powerTransformer,
                    powerTransformerPort,
                    sibling,
                    siblingPort
                ).mapLeft { throw VoltageValidationException(it, "Error while voltage validation") }
            } else if (sibling.isThereVoltageLevel()) {
                validateVoltagesForPowerTransformerAndOtherEquipment(
                    powerTransformer,
                    powerTransformerPort,
                    sibling
                ).mapLeft { throw VoltageValidationException(it, "Error while voltage validation") }
            }
        }
    }

    private fun validateVoltagesForTwoPowerTransformerInScheme(
        powerTransformer: EquipmentNodeDomain,
        powerTransformerPort: PortDto,
        siblingPowerTransformer: EquipmentNodeDomain,
        siblingPowerTransformerPort: PortDto
    ): Either<VoltageValidationError, Unit> {
        val voltageValueInKilovolts = powerTransformer.getVoltageInKilovoltsByTransformerType(powerTransformerPort)
        val transformerVoltageLevel = findNearestVoltageLevelByVoltageInKilovolts(voltageValueInKilovolts)

        val siblingVoltageValueInKilovolts =
            siblingPowerTransformer.getVoltageInKilovoltsByTransformerType(siblingPowerTransformerPort)
        val siblingVoltageLevel = findNearestVoltageLevelByVoltageInKilovolts(siblingVoltageValueInKilovolts)

        return if (transformerVoltageLevel != siblingVoltageLevel) {
            VoltageValidationError.ConnectedNodesHaveDifferentVoltageLevels(
                powerTransformer.id,
                powerTransformer.getName(),
                siblingPowerTransformer.getName(),
                voltageValueInKilovolts,
                siblingVoltageValueInKilovolts
            ).left()
        } else {
            Unit.right()
        }
    }

    private fun validateVoltagesForPowerTransformerAndOtherEquipment(
        powerTransformer: EquipmentNodeDomain,
        powerTransformerPort: PortDto,
        equipmentNode: EquipmentNodeDomain
    ): Either<VoltageValidationError, Unit> {
        val voltageValueInKilovolts = powerTransformer.getVoltageInKilovoltsByTransformerType(powerTransformerPort)
        val equipmentNodeVoltageInKilovolts = equipmentNode.getVoltageInKilovolts()

        val powerTransformerVoltageLevel = findNearestVoltageLevelByVoltageInKilovolts(voltageValueInKilovolts)
        val equipmentNodeVoltageLevel = findNearestVoltageLevelByVoltageInKilovolts(equipmentNodeVoltageInKilovolts)
        return if (powerTransformerVoltageLevel.voltageInKilovolts != equipmentNodeVoltageLevel.voltageInKilovolts) {
            VoltageValidationError.ConnectedNodesHaveDifferentVoltageLevels(
                powerTransformer.id,
                powerTransformer.getName(),
                equipmentNode.getNameOrId(),
                voltageValueInKilovolts,
                equipmentNodeVoltageInKilovolts
            ).left()
        } else {
            Unit.right()
        }
    }
}
