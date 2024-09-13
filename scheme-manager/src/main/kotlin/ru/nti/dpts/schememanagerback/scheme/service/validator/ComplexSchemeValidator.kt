package ru.nti.dpts.schememanagerback.scheme.service.validator

import arrow.core.Either
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.ValidationError

interface ComplexSchemeValidator {
    fun validate(scheme: SchemeDomain): Either<ValidationError, Unit>
}
