package ru.nti.dpts.schememanagerback.user.persister

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

const val USER_COLLECTION = "user"

@Document(collection = USER_COLLECTION)
class UserDoc(
    @Id
    val keycloakUserId: UUID,
    var firstName: String,
    var lastName: String,
    val companyId: UUID
)
