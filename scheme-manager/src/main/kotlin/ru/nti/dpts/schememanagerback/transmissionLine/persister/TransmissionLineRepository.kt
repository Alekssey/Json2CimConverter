package ru.nti.dpts.schememanagerback.transmissionLine.persister

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface TransmissionLineRepository : MongoRepository<TransmissionLineTemplateDoc, UUID> {
    fun findByCompanyId(companyId: String): Collection<TransmissionLineTemplateDoc>
    fun findAllByCompanyIdAndMatrices3x3IsNotNull(companyId: String): Collection<TransmissionLineTemplateDoc>
    fun findAllByCompanyIdAndMatrices6x6IsNotNull(companyId: String): Collection<TransmissionLineTemplateDoc>
}
