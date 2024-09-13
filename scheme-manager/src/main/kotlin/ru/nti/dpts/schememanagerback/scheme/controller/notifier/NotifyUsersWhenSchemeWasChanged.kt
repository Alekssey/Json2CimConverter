package ru.nti.dpts.schememanagerback.scheme.controller.notifier

import jakarta.annotation.PostConstruct
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.application.common.events.DomainEventListener
import ru.nti.dpts.schememanagerback.scheme.domain.events.EventPublisherImpl
import ru.nti.dpts.schememanagerback.scheme.domain.events.SchemeUpdatedEvent
import ru.nti.dpts.schememanagerback.scheme.domain.events.SchemeValidatedEvent

//@Component
class NotifyUsersWhenSchemeWasUpdated(
//    private val domainEventPublisher: EventPublisherImpl,
    private val simpMessageSendingOperations: SimpMessageSendingOperations
) : DomainEventListener<SchemeUpdatedEvent> {

//    @PostConstruct
    fun registerListener() {
//        domainEventPublisher.registerListener(this)
    }

    override fun eventType() = SchemeUpdatedEvent::class

    override fun handle(event: SchemeUpdatedEvent) {
        simpMessageSendingOperations.convertAndSend(
            Routes.publishSchemeStatus(event.projectId),
            SchemeStatusNotifierDto.from(event)
        )
    }
}

//@Component
class NotifyUsersWhenSchemeWasValidated(
    private val domainEventPublisher: EventPublisherImpl,
    private val simpMessageSendingOperations: SimpMessageSendingOperations
) : DomainEventListener<SchemeValidatedEvent> {

    @PostConstruct
    fun registerListener() {
//        domainEventPublisher.registerListener(this)
    }

    override fun eventType() = SchemeValidatedEvent::class

    override fun handle(event: SchemeValidatedEvent) {
        simpMessageSendingOperations.convertAndSend(
            Routes.publishSchemeStatus(event.projectId),
            SchemeStatusNotifierDto.from(event)
        )
    }
}

data class SchemeStatusNotifierDto(
    val id: String,
    val updatedVersion: Long,
    val status: Status
) {
    enum class Status {
        SCHEME_VALIDATED,
        SCHEME_UPDATED
    }

    companion object {
        fun from(event: SchemeValidatedEvent) =
            SchemeStatusNotifierDto(
                event.projectId,
                event.updatedVersion,
                Status.SCHEME_VALIDATED
            )

        fun from(event: SchemeUpdatedEvent) =
            SchemeStatusNotifierDto(
                event.projectId,
                event.version,
                Status.SCHEME_UPDATED
            )
    }
}
