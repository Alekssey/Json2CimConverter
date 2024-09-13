package ru.nti.dpts.schememanagerback.scheme.service.validator

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.springframework.stereotype.Service
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.SchemeValidator
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.ValidationError

/**
 * Validators throw IllegalArgumentException if there is scheme error, that user can't fix.
 * So these types of errors relates to developers.
 */
@Service
class ValidatorService(
    schemeValidators: List<SchemeValidator>
) : ComplexSchemeValidator {
    private val schemeValidators: List<SchemeValidator> = schemeValidators.sortedBy {
        it.getLevelNumber()
    }

    override fun validate(scheme: SchemeDomain): Either<ValidationError, Unit> {
        schemeValidators.forEach { it.validate(scheme).mapLeft { error -> return error.left() } }
        return Unit.right()
    }
}
