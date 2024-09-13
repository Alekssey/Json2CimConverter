package ru.nti.dpts.schememanagerback.scheme.controller.locker

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Component
@EnableScheduling
class SchemeEditorInMemoryHolder(
    private val eventPublisher: ApplicationEventPublisher
) : SchemeEditorsHolder {

    companion object {
        private val logger = LoggerFactory.getLogger(SchemeEditorsHolder::class.java)
    }

    private val lockedProjectIdToUserInfoMap: ConcurrentHashMap<String, UserEditorInfo> = ConcurrentHashMap()

    private val sessionLock = Any()

    override fun getUserEditorByProject(projectId: String): String? {
        return lockedProjectIdToUserInfoMap[projectId]?.userId
    }

    override fun updateProjectEditorOrThrowException(projectId: String, userId: String) {
        if (!projectAlreadyLocked(projectId)) {
            lockProjectBySpecificUser(projectId, userId)
            return
        } else if (iaPresentedUserLockProject(projectId, userId)) {
            updateLastActivityInfo(projectId)
            return
        }
        throw ProjectAlreadyEditByAnotherUserException()
    }

    private fun projectAlreadyLocked(projectId: String): Boolean {
        return lockedProjectIdToUserInfoMap.containsKey(projectId)
    }

    private fun lockProjectBySpecificUser(projectId: String, userId: String) {
        synchronized(sessionLock) {
            lockedProjectIdToUserInfoMap[projectId] = UserEditorInfo(
                userId
            )
        }
        eventPublisher.publishEvent(
            UserAssignedToProjectEvent(
                userId,
                projectId
            )
        )
    }

    private fun updateLastActivityInfo(projectId: String) {
        synchronized(sessionLock) {
            lockedProjectIdToUserInfoMap[projectId]?.updateLastActivity()
        }
    }

    private fun iaPresentedUserLockProject(projectId: String, userId: String): Boolean {
        return lockedProjectIdToUserInfoMap[projectId]?.let {
            it.userId == userId
        } ?: false
    }

    private fun removeTrackedUserFromScheme(projectId: String) {
        val removedSessionEditor: UserEditorInfo?
        synchronized(sessionLock) {
            removedSessionEditor = lockedProjectIdToUserInfoMap.remove(projectId)
        }
        if (removedSessionEditor != null) {
            eventPublisher.publishEvent(
                UserUnassignedFromProjectEvent(
                    removedSessionEditor.userId
                )
            )
        }
    }

    @EventListener
    fun handleSessionDisconnect(event: SessionDisconnectEvent) {
        logger.trace("Web socket session associated with user id: {} was closed", event.user?.name)
        val projectWhereUserDisconnect =
            lockedProjectIdToUserInfoMap.filter { entry -> entry.value.userId == event.user?.name }.map { it.key }
                .firstOrNull()
        projectWhereUserDisconnect?.let {
            removeTrackedUserFromScheme(it)
        }
    }

    private class UserEditorInfo(val userId: String) {
        private val deadTime = Duration.ofMinutes(2L)

        private var lastActivityTime: Instant = Instant.now()

        fun updateLastActivity() {
            lastActivityTime = Instant.now()
        }

        fun isExpired(currentInstant: Instant): Boolean {
            return currentInstant.minus(deadTime).isAfter(lastActivityTime)
        }
    }

    @Scheduled(fixedRate = 30_000L)
    fun checkDeadTUsersSessionAndRemove() {
        val now = Instant.now()
        lockedProjectIdToUserInfoMap
            .filter { entry -> entry.value.isExpired(now) }
            .forEach {
                removeTrackedUserFromScheme(it.key)
            }
    }
}

data class UserAssignedToProjectEvent(val user: String, val projectId: String)

data class UserUnassignedFromProjectEvent(val projectId: String)
