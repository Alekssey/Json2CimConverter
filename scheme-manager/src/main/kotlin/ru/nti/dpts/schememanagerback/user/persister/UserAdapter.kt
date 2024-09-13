package ru.nti.dpts.schememanagerback.user.persister

import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.user.domain.User
import ru.nti.dpts.schememanagerback.user.domain.adapter.UserExtractor
import ru.nti.dpts.schememanagerback.user.domain.adapter.UserPersister
import java.util.*
import kotlin.jvm.optionals.getOrNull

//@Component
class UserAdapter(
    private val userRepository: UserRepository
) : UserPersister, UserExtractor {

    override fun save(user: User) {
        userRepository.save(user.toDoc())
    }

    override fun saveUsersIfDontExist(users: List<User>) {
        userRepository.findAll()
            .map { it.keycloakUserId }
            .let { existUserIds ->
                users
                    .filter { user ->
                        !existUserIds.contains(user.keycloakUserId)
                    }
                    .map { it.toDoc() }
                    .let { userRepository.saveAll(it) }
            }
    }

    override fun deleteById(id: UUID) {
        userRepository.deleteById(id)
    }

    override fun getById(userId: UUID): User? {
        return userRepository.findById(userId).getOrNull()?.toDomain()
    }

    private fun User.toDoc() = UserDoc(
        keycloakUserId = this.keycloakUserId,
        firstName = this.firstName,
        lastName = this.lastName,
        companyId = this.companyId
    )

    private fun UserDoc.toDomain() = User(
        keycloakUserId = this.keycloakUserId,
        firstName = this.firstName,
        lastName = this.lastName,
        companyId = this.companyId
    )
}
