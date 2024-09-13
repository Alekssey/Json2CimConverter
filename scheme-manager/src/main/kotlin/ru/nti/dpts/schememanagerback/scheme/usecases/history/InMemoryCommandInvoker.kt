package ru.nti.dpts.schememanagerback.scheme.usecases.history

import arrow.core.Either
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.application.common.events.DomainEventPublisher
import ru.nti.dpts.schememanagerback.scheme.domain.CommandExecutionError
import ru.nti.dpts.schememanagerback.scheme.domain.command.Command
import ru.nti.dpts.schememanagerback.scheme.domain.command.CommandInvoker
import ru.nti.dpts.schememanagerback.scheme.domain.command.SchemeEditorCommandExecutor
import ru.nti.dpts.schememanagerback.scheme.domain.command.UnredoableHistoryCleaner
import java.util.concurrent.ConcurrentHashMap

@Component
class InMemoryCommandInvoker(
//    private val commandExecutor: SchemeEditorCommandExecutor,
//    private val domainEventPublisher: DomainEventPublisher
)  {
    private val historyPerProject = ConcurrentHashMap<String, HistoryProvider>()

//    fun get(projectId: String): HistoryProvider {
////        return historyPerProject.getOrPut(projectId) { ProjectHistory(projectId, commandExecutor, domainEventPublisher) }
//    }

//    override fun invoke(command: Command): Either<CommandExecutionError, Unit> {
//        return get(command.projectId).execute(command)
//    }
//
//    override fun undo(projectId: String, userId: String): Either<CommandExecutionError, Command> {
//        val historyProvider = get(projectId)
//        return historyProvider.undo(userId)
//    }
//
//    override fun redo(projectId: String, userId: String): Either<CommandExecutionError, Command> {
//        val historyProvider = get(projectId)
//        return historyProvider.redo(userId)
//    }
//
//    override fun extract(projectId: String, userId: String): UnredoableSnapshot {
//        return get(projectId).info(userId)
//    }
//
//    override fun clear(projectId: String) {
//        historyPerProject.remove(projectId)
//    }
}

open class UnredoableSnapshot(
    val hasUndo: Boolean,
    val hasRedo: Boolean
)
