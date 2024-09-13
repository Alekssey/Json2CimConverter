package ru.nti.dpts.schememanagerback.user.service

import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import ru.nti.dpts.schememanagerback.client.IntegrationPlatformClient
import ru.nti.dpts.schememanagerback.user.domain.adapter.UserPersister

//@Service
class UserManager(
    private val client: IntegrationPlatformClient,
    private val userPersister: UserPersister
) {

    @EventListener(ApplicationStartedEvent::class)
    fun retrieve() {
        client
            .getAllUsers()
            .also { userPersister.saveUsersIfDontExist(it) }
    }
}
