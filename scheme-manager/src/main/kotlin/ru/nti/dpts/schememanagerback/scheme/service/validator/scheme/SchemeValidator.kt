package ru.nti.dpts.schememanagerback.scheme.service.validator.scheme

import arrow.core.Either
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain

interface SchemeValidator {
    fun validate(scheme: SchemeDomain): Either<ValidationError, Unit>

    fun getLevelNumber(): Int
}

interface ValidationError
