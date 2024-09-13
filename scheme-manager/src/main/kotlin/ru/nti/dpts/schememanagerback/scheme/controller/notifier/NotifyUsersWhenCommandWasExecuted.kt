package ru.nti.dpts.schememanagerback.scheme.controller.notifier

import jakarta.annotation.PostConstruct
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.application.common.events.DomainEventListener
import ru.nti.dpts.schememanagerback.scheme.controller.*
import ru.nti.dpts.schememanagerback.scheme.domain.command.*
import ru.nti.dpts.schememanagerback.scheme.domain.events.EventPublisherImpl
import ru.nti.dpts.schememanagerback.scheme.domain.events.SchemeEditorCommandExecutedEvent

@Component
class NotifyUsersWhenCommandWasExecuted(
//    private val domainEventPublisher: EventPublisherImpl,
    private val simpMessageSendingOperations: SimpMessageSendingOperations
) : DomainEventListener<SchemeEditorCommandExecutedEvent> {

    @PostConstruct
    fun registerListener() {
//        domainEventPublisher.registerListener(this)
    }

    override fun eventType() = SchemeEditorCommandExecutedEvent::class

    // TODO add single command as possible dto
    override fun handle(event: SchemeEditorCommandExecutedEvent) {
        if (event.command is BatchCommand) {
            simpMessageSendingOperations.convertAndSend(
                Routes.publishCommand(event.projectId),
                CommandBatchResponseDto.from(event.command)
            )
        }
    }
}

data class CommandBatchResponseDto(
    val id: String,
    val userId: String,
    val commands: List<CommandDto>
) {
    companion object {
        fun from(batch: BatchCommand) =
            CommandBatchResponseDto(
                batch.id,
                batch.userId,
                batch.commands.map { it.toDto() }
            )
    }
}

fun Command.toDto(): CommandDto {
    return when (this) {
        is CreateNodeCommand -> CreateNodeCommandDto(this.type, body)
        is ChangeNodeCoordsCommand -> ChangeNodeCoordsCommandDto(this.type, body)
        is ChangeNodeDimensionsCommand -> ChangeNodeDimensionsCommandDto(this.type, body)
        is ChangeNodeRotationCommand -> ChangeNodeRotationCommandDto(this.type, body)
        is ChangeNodeParamsCommand -> ChangeNodeParamsCommandDto(this.type, body)
        is ChangeNodePortsCommand -> ChangeNodePortsCommandDto(this.type, body)
        is DeleteNodeCommand -> DeleteNodeCommandDto(this.type, body)

        is UpdateLinkCommand -> UpdateLinkCommandDto(this.type, body)
        is CreateLinkCommand -> CreateLinkCommandDto(this.type, body)
        is DeleteLinkCommand -> DeleteLinkCommandDto(this.type, body)
        else -> error("Unable to convert node to dto")
    }
}
