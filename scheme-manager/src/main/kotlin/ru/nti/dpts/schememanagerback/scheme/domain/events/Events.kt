package ru.nti.dpts.schememanagerback.scheme.domain.events

import ru.nti.dpts.schememanagerback.application.common.base.DomainEvent
import ru.nti.dpts.schememanagerback.scheme.domain.command.Command

data class SchemeEditorCommandExecutedEvent(
    val projectId: String,
    val userId: String,
    val command: Command
) : DomainEvent()

data class SchemeValidatedEvent(
    val projectId: String,
    val updatedVersion: Long
) : DomainEvent()

data class SchemeUpdatedEvent(
    val projectId: String,
    val userId: String,
    val version: Long
) : DomainEvent()

data class UnredoableInfoWasUpdatedEvent(
    val projectId: String,
    val userId: String
) : DomainEvent()
