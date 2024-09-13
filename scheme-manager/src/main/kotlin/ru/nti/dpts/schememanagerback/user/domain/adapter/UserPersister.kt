package ru.nti.dpts.schememanagerback.user.domain.adapter

import ru.nti.dpts.schememanagerback.user.domain.User
import java.util.*

interface UserPersister {
    fun save(user: User)
    fun saveUsersIfDontExist(users: List<User>)
    fun deleteById(id: UUID)
}
