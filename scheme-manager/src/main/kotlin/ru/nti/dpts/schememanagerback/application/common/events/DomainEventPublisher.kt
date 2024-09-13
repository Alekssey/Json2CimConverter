package ru.nti.dpts.schememanagerback.application.common.events

import ru.nti.dpts.schememanagerback.application.common.base.DomainEvent

interface DomainEventPublisher {
    fun publish(events: Collection<DomainEvent>)
    fun publish(event: DomainEvent)
}
