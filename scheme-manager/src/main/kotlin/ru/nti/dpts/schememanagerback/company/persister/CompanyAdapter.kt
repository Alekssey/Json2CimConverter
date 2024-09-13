package ru.nti.dpts.schememanagerback.company.persister

import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.company.domain.Company
import ru.nti.dpts.schememanagerback.company.domain.adapter.CompanyExtractor
import ru.nti.dpts.schememanagerback.company.domain.adapter.CompanyPersister
import java.util.*
import kotlin.jvm.optionals.getOrNull

//@Component
class CompanyAdapter(
    private val companyRepository: CompanyRepository
) : CompanyExtractor, CompanyPersister {
    override fun getById(id: UUID): Company? {
        return companyRepository.findById(id).getOrNull()?.toDomain()
    }

    override fun save(company: Company) {
        companyRepository.save(company.toDoc())
    }

    override fun saveCompaniesIfDontExist(companies: List<Company>) {
        companyRepository.findAll()
            .map { it.id }
            .let { existCompaniesIds ->
                companies
                    .filter { company ->
                        !existCompaniesIds.contains(company.id)
                    }
                    .map { it.toDoc() }
                    .let { companyRepository.saveAll(it) }
            }
    }

    override fun deleteById(id: UUID) {
        companyRepository.deleteById(id)
    }

    fun Company.toDoc() = CompanyDoc(
        id = this.id,
        name = this.name
    )

    fun CompanyDoc.toDomain() = Company(
        id = this.id,
        name = this.name
    )
}
