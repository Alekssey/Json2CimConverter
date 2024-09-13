package ru.nti.dpts.schememanagerback.scheme.usecases.history

import arrow.core.Either
import ru.nti.dpts.schememanagerback.scheme.domain.CommandExecutionError
import ru.nti.dpts.schememanagerback.scheme.domain.command.Command

interface HistoryProvider {
    fun info(userId: String): UnredoableSnapshot
    fun execute(command: Command): Either<CommandExecutionError, Unit>
    fun undo(userId: String): Either<CommandExecutionError, Command>
    fun redo(userId: String): Either<CommandExecutionError, Command>
    fun clear()
}
