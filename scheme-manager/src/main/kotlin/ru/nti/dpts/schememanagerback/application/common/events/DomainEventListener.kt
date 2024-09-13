package ru.nti.dpts.schememanagerback.application.common.events

import ru.nti.dpts.schememanagerback.application.common.base.DomainEvent
import kotlin.reflect.KClass

interface DomainEventListener<T : DomainEvent> {

    fun eventType(): KClass<T>

    fun handle(event: T)
}
