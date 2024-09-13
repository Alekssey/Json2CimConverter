package ru.nti.dpts.schememanagerback.application.common.base

import java.time.Instant
import java.util.*

open class DomainEvent protected constructor() {
    val id = EventId.generate()
    val created = Instant.now()
}

data class EventId internal constructor(val value: UUID) {
    companion object {
        fun generate(): EventId = EventId(UUID.randomUUID())
    }
}
