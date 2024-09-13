package ru.nti.dpts.schememanagerback.user.service

import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.nti.dpts.schememanagerback.user.domain.User
import ru.nti.dpts.schememanagerback.user.domain.adapter.UserExtractor
import ru.nti.dpts.schememanagerback.user.domain.adapter.UserPersister
import ru.nti.dtps.proto.UserEventMsg
import java.util.*

@Service
class UserService(
//    private val userPersister: UserPersister,
//    private val userExtractor: UserExtractor
) {

//    private val log = LoggerFactory.getLogger(javaClass)
//
//    @Transactional
//    fun onEvent(event: UserEventMsg) {
//        when (event.eventType) {
//            UserEventMsg.EventType.CREATE, UserEventMsg.EventType.UPDATE -> userPersister.save(event.mapToDomain())
//            UserEventMsg.EventType.DELETE -> userPersister.deleteById(UUID.fromString(event.keycloakUserId))
//            UserEventMsg.EventType.UNRECOGNIZED -> log.warn("Unrecognized company event type has been received")
//            null -> throw NullPointerException("User event type cannot be null")
//        }
//    }
//
//    @Transactional(readOnly = true)
//    fun getCurrentUser(): User {
//        val auth = SecurityContextHolder.getContext().authentication
//        val principal = auth.principal as Jwt
//        val userIdString = principal.getClaimAsString("sub")
//        return userExtractor.getById(UUID.fromString(userIdString)) ?: throw AccessDeniedException(
//            "User with id $userIdString not found"
//        )
//    }
//
//    private fun UserEventMsg.mapToDomain() =
//        User(
//            keycloakUserId = UUID.fromString(this.keycloakUserId),
//            firstName = this.firstName,
//            lastName = this.lastName,
//            companyId = UUID.fromString(this.companyId)
//        )
}
