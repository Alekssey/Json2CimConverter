package ru.nti.dpts.schememanagerback.scheme.controller.notifier

import jakarta.annotation.PostConstruct
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.application.common.events.DomainEventListener
import ru.nti.dpts.schememanagerback.scheme.domain.events.EventPublisherImpl
import ru.nti.dpts.schememanagerback.scheme.domain.events.SchemeUpdatedEvent
import ru.nti.dpts.schememanagerback.scheme.domain.events.UnredoableInfoWasUpdatedEvent
import ru.nti.dpts.schememanagerback.scheme.usecases.history.UnredoableSnapshotProvider

//@Component
class NotifyUserUnredoableInfoWhenCommandExecuted(
    private val domainEventPublisher: EventPublisherImpl,
    private val unredoableSnapshotProvider: UnredoableSnapshotProvider,
    private val simpMessageSendingOperations: SimpMessageSendingOperations
) : DomainEventListener<UnredoableInfoWasUpdatedEvent> {

//    @PostConstruct
    fun registerListener() {
//        domainEventPublisher.registerListener(this)
    }

    override fun eventType() = UnredoableInfoWasUpdatedEvent::class

    override fun handle(event: UnredoableInfoWasUpdatedEvent) {
        val unredoable = unredoableSnapshotProvider.extract(event.projectId, event.userId)
        simpMessageSendingOperations.convertAndSendToUser(
            event.userId,
            Routes.publishUnredoable(event.projectId),
            UnredoableStompDto(
                event.projectId,
                event.userId,
                unredoable.hasUndo,
                unredoable.hasRedo
            )
        )
    }
}

@Component
class NotifyUserUnredoableInfoWhenSchemeUpdated(
//    private val domainEventPublisher: EventPublisherImpl,
    private val simpMessageSendingOperations: SimpMessageSendingOperations
) : DomainEventListener<SchemeUpdatedEvent> {

    @PostConstruct
    fun registerListener() {
//        domainEventPublisher.registerListener(this)
    }

    override fun eventType() = SchemeUpdatedEvent::class

    override fun handle(event: SchemeUpdatedEvent) {
        simpMessageSendingOperations.convertAndSend(
            Routes.publishSchemeStatus(event.projectId),
            UnredoableStompDto(
                event.projectId,
                event.userId,
                hasUndo = false,
                hasRedo = false
            )
        )
    }
}

data class UnredoableStompDto(
    val projectId: String,
    val userId: String,
    val hasUndo: Boolean,
    val hasRedo: Boolean
)
