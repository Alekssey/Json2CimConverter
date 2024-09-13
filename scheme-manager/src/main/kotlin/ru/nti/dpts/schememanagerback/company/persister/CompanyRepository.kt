package ru.nti.dpts.schememanagerback.company.persister

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface CompanyRepository : MongoRepository<CompanyDoc, UUID>
