package ru.nti.dpts.schememanagerback.scheme.usecases

import arrow.core.Either
import ru.nti.dpts.schememanagerback.scheme.domain.command.ValidateSchemeCommand
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.ValidationError

interface ValidateScheme {
    fun execute(command: ValidateSchemeCommand): Either<ValidationError, Unit>
}
