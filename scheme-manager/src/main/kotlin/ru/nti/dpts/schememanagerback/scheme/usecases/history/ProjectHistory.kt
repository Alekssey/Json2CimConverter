package ru.nti.dpts.schememanagerback.scheme.usecases.history

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import ru.nti.dpts.schememanagerback.application.common.events.DomainEventPublisher
import ru.nti.dpts.schememanagerback.scheme.domain.CommandExecutionError
import ru.nti.dpts.schememanagerback.scheme.domain.command.Command
import ru.nti.dpts.schememanagerback.scheme.domain.command.SchemeEditorCommandExecutor
import ru.nti.dpts.schememanagerback.scheme.domain.events.UnredoableInfoWasUpdatedEvent
import java.util.*

class ProjectHistory(
    val projectId: String,
    private val schemeEditorCommandExecutor: SchemeEditorCommandExecutor,
    private val domainEventPublisher: DomainEventPublisher
) : HistoryProvider {
    private val undoStack = LinkedList<Command>()
    private val redoStack = LinkedList<Command>()
    private val maxStackSize = 300

    override fun info(userId: String): UnredoableSnapshot {
        return UnredoableSnapshot(
            hasUndo = undoStack.any { it.userId == userId },
            hasRedo = redoStack.any { it.userId == userId }
        )
    }

    override fun execute(command: Command): Either<CommandExecutionError, Unit> {
        return schemeEditorCommandExecutor.execute(command)
            .fold(
                { it.left() },
                {
                    if (undoStack.size >= maxStackSize) {
                        undoStack.removeLast()
                    }
                    undoStack.addFirst(command)
                    redoStack.removeIf { redoCommand -> redoCommand.userId == command.userId }
                    domainEventPublisher.publish(UnredoableInfoWasUpdatedEvent(projectId, command.userId))
                    Unit.right()
                }
            )
    }

    override fun undo(userId: String): Either<CommandExecutionError, Command> {
        val command = undoStack.firstOrNull { it.userId == userId }

        return command?.undo()?.run {
            schemeEditorCommandExecutor.execute(this).fold(
                { it.left() },
                {
                    if (redoStack.size >= maxStackSize) {
                        redoStack.removeLast()
                    }
                    redoStack.addFirst(command)
                    undoStack.remove(command)
                    domainEventPublisher.publish(UnredoableInfoWasUpdatedEvent(projectId, userId))
                    this.right()
                }
            )
        } ?: throw IllegalStateException("No undo command to execute found")
    }

    override fun redo(userId: String): Either<CommandExecutionError, Command> {
        val command = redoStack.firstOrNull { it.userId == userId }
        return command?.run {
            schemeEditorCommandExecutor.execute(this).fold(
                { it.left() },
                {
                    command.undo()?.apply {
                        if (undoStack.size >= maxStackSize) {
                            undoStack.removeLast()
                        }
                        undoStack.addFirst(command)
                    }
                    redoStack.remove(command)
                    domainEventPublisher.publish(UnredoableInfoWasUpdatedEvent(projectId, userId))
                    this.right()
                }
            )
        } ?: throw IllegalStateException("No undo command to execute found")
    }

    override fun clear() {
        undoStack.clear()
        redoStack.clear()
    }
}
