package ru.nti.dpts.schememanagerback.user.domain.adapter

import ru.nti.dpts.schememanagerback.user.domain.User
import java.util.*

interface UserExtractor {
    fun getById(userId: UUID): User?
}
