package ru.nti.dpts.schememanagerback.scheme.controller.notifier

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.scheme.controller.ProjectEditorInfo
import ru.nti.dpts.schememanagerback.scheme.controller.locker.UserAssignedToProjectEvent
import ru.nti.dpts.schememanagerback.scheme.controller.locker.UserUnassignedFromProjectEvent

@Component
class NotifyUsersWhenSchemeLockStatusWasChanged(
    private val simpMessageSendingOperations: SimpMessageSendingOperations
) {

    companion object {
        private val logger = LoggerFactory.getLogger(NotifyUsersWhenSchemeLockStatusWasChanged::class.java)
    }

    @EventListener(UserAssignedToProjectEvent::class)
    fun handleUserLockedProjectEvent(event: UserAssignedToProjectEvent) {
        logger.debug("User lock scheme editing {}", event)
        simpMessageSendingOperations.convertAndSend(
            "/queue/scheme.${event.projectId}.lock",
            ProjectEditorInfo.from(event.user, event.projectId)
        )
    }

    @EventListener(UserUnassignedFromProjectEvent::class)
    fun handleUserUnlockedProjectEvent(event: UserUnassignedFromProjectEvent) {
        logger.debug("User unlock scheme editing {}", event)
        simpMessageSendingOperations.convertAndSend(
            "/queue/scheme.${event.projectId}.lock",
            ProjectEditorInfo.from(null, event.projectId)
        )
    }
}
