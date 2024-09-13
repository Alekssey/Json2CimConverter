package ru.nti.dpts.schememanagerback.user.domain

import java.util.*

class User(
    val keycloakUserId: UUID,
    val firstName: String,
    val lastName: String,
    val companyId: UUID
)
