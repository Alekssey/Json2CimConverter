package ru.nti.dpts.schememanagerback.company.domain.adapter

import ru.nti.dpts.schememanagerback.company.domain.Company
import java.util.*

interface CompanyExtractor {
    fun getById(id: UUID): Company?
}
