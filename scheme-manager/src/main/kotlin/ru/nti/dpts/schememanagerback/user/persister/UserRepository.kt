package ru.nti.dpts.schememanagerback.user.persister

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface UserRepository : MongoRepository<UserDoc, UUID>
