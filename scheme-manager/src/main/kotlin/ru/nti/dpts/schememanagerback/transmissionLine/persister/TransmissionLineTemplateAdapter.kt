package ru.nti.dpts.schememanagerback.transmissionLine.persister

import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.transmissionLine.domain.TransmissionLineDoubleCircuitTemplate
import ru.nti.dpts.schememanagerback.transmissionLine.domain.TransmissionLineSingleCircuitTemplate
import ru.nti.dpts.schememanagerback.transmissionLine.domain.TransmissionLineTemplate
import ru.nti.dpts.schememanagerback.transmissionLine.domain.adapter.TransmissionLineTemplateExtractor
import ru.nti.dpts.schememanagerback.transmissionLine.domain.adapter.TransmissionLineTemplatePersister
import java.util.*

//@Component
class TransmissionLineTemplateAdapter(
    private val transmissionLineRepository: TransmissionLineRepository
) : TransmissionLineTemplatePersister, TransmissionLineTemplateExtractor {
    override fun findByCompanyId(companyId: String): Collection<TransmissionLineTemplate> {
        return transmissionLineRepository.findByCompanyId(companyId).map { it.toDomain() }
    }

    override fun findAllByCompanyIdAndMatrices3x3IsNotNull(
        companyId: String
    ): Collection<TransmissionLineSingleCircuitTemplate> {
        return transmissionLineRepository
            .findAllByCompanyIdAndMatrices3x3IsNotNull(companyId)
            .map { it.toSingleCircuitDomain() }
    }

    override fun findAllByCompanyIdAndMatrices6x6IsNotNull(
        companyId: String
    ): Collection<TransmissionLineDoubleCircuitTemplate> {
        return transmissionLineRepository
            .findAllByCompanyIdAndMatrices6x6IsNotNull(companyId)
            .map { it.toDoubleCircuitDomain() }
    }

    override fun save(transmissionLineTemplate: TransmissionLineTemplate) {
        transmissionLineRepository.save(transmissionLineTemplate.toDoc())
    }

    override fun deleteById(id: UUID) {
        transmissionLineRepository.deleteById(id)
    }

    private fun TransmissionLineTemplate.toDoc() =
        TransmissionLineTemplateDoc(
            this.id,
            this.name,
            this.frequency,
            this.lastModifiedAt,
            this.matrices3x3,
            this.matrices6x6,
            this.companyId
        )

    private fun TransmissionLineTemplateDoc.toDomain() = TransmissionLineTemplate(
        id = id,
        name = name,
        frequency = frequency,
        lastModifiedAt = lastModifiedAt,
        matrices3x3 = matrices3x3,
        matrices6x6 = matrices6x6,
        companyId = companyId
    )

    private fun TransmissionLineTemplateDoc.toSingleCircuitDomain() =
        TransmissionLineSingleCircuitTemplate(
            this.id,
            this.name,
            this.frequency,
            this.lastModifiedAt,
            this.matrices3x3!!,
            this.companyId
        )

    private fun TransmissionLineTemplateDoc.toDoubleCircuitDomain() =
        TransmissionLineDoubleCircuitTemplate(
            this.id,
            this.name,
            this.frequency,
            this.lastModifiedAt,
            this.matrices6x6!!,
            this.companyId
        )
}
