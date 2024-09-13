package ru.nti.dpts.schememanagerback.scheme.service.validator.scheme

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId

@Component
class RequiredEquipmentsValidator : AbstractSchemeValidator(Level.FIRST) {

    private val sources = setOf(
        EquipmentLibId.POWER_SYSTEM_EQUIVALENT,
        EquipmentLibId.SYNCHRONOUS_GENERATOR,
        EquipmentLibId.SOURCE_OF_ELECTROMOTIVE_FORCE_DC
    )

    override fun validate(scheme: SchemeDomain): Either<SchemeDoesNotContainsRequiredEquipmentError, Unit> {
        val schemeEquipmentLibIds = scheme.nodes.values.map { it.libEquipmentId }

        if (!schemeEquipmentLibIds.any { sources.contains(it) }) {
            return SchemeDoesNotContainsRequiredEquipmentError(sources).left()
        }
        return Unit.right()
    }
}

class SchemeDoesNotContainsRequiredEquipmentError(
    val requiredEquipment: Set<EquipmentLibId>
) : ValidationError
