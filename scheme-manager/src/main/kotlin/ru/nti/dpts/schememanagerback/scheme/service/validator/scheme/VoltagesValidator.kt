package ru.nti.dpts.schememanagerback.scheme.service.validator.scheme

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.voltages.VoltagesValidatorWithPowerTransformer
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.voltages.VoltagesValidatorWithSources
import ru.nti.dtps.equipment.meta.info.extension.isPowerTransformer

@Component
class VoltagesValidator(
    private val voltagesValidatorWithPowerTransformer: VoltagesValidatorWithPowerTransformer,
    private val voltagesValidatorWithSources: VoltagesValidatorWithSources
) : AbstractSchemeValidator(Level.FOURTH) {

    override fun validate(scheme: SchemeDomain): Either<VoltageValidationError, Unit> {
        return selectVoltagesValidator(scheme)
    }

    private fun selectVoltagesValidator(scheme: SchemeDomain): Either<VoltageValidationError, Unit> {
        when (scheme.nodes.values.any { it.libEquipmentId.isPowerTransformer() }) {
            true -> voltagesValidatorWithPowerTransformer.validate(scheme).mapLeft { return it.left() }
            else -> voltagesValidatorWithSources.validate(scheme).mapLeft { return it.left() }
        }
        return Unit.right()
    }
}

open class VoltageValidationError : ValidationError {
    class ConnectedNodesHaveDifferentVoltageLevels(
        val equipmentId: String,
        val equipmentSourceName: String,
        val equipmentTargetName: String,
        val sourceEquipmentVoltage: Double,
        val targetEquipmentVoltage: Double
    ) : VoltageValidationError()
}

class VoltageValidationException(val error: VoltageValidationError, message: String?) : RuntimeException(message)
