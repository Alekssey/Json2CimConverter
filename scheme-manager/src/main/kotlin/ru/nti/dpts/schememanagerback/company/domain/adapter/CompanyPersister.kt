package ru.nti.dpts.schememanagerback.company.domain.adapter

import ru.nti.dpts.schememanagerback.company.domain.Company
import java.util.*

interface CompanyPersister {
    fun save(company: Company)
    fun saveCompaniesIfDontExist(companies: List<Company>)
    fun deleteById(id: UUID)
}
