package ru.nti.dpts.schememanagerback.scheme.domain.command

import arrow.core.Either
import ru.nti.dpts.schememanagerback.scheme.domain.CommandExecutionError
import ru.nti.dpts.schememanagerback.scheme.domain.ProjectAggregate

interface SchemeEditorCommandExecutor {
    fun execute(command: Command): Either<CommandExecutionError, ProjectAggregate>
}
