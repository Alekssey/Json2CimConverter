package ru.nti.dpts.schememanagerback.scheme.domain.command

import arrow.core.Either
import ru.nti.dpts.schememanagerback.scheme.domain.CommandExecutionError

interface CommandInvoker {
    fun invoke(command: Command): Either<CommandExecutionError, Unit>
    fun undo(projectId: String, userId: String): Either<CommandExecutionError, Command>
    fun redo(projectId: String, userId: String): Either<CommandExecutionError, Command>
}
