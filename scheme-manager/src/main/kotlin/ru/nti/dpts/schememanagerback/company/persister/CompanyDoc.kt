package ru.nti.dpts.schememanagerback.company.persister

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

const val COMPANY_COLLECTION = "company"

@Document(collection = COMPANY_COLLECTION)
class CompanyDoc(
    @Id
    val id: UUID,
    var name: String
)
