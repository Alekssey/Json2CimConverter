package ru.nti.dpts.schememanagerback.transmissionLine.domain.adapter

import ru.nti.dpts.schememanagerback.transmissionLine.domain.TransmissionLineDoubleCircuitTemplate
import ru.nti.dpts.schememanagerback.transmissionLine.domain.TransmissionLineSingleCircuitTemplate
import ru.nti.dpts.schememanagerback.transmissionLine.domain.TransmissionLineTemplate

interface TransmissionLineTemplateExtractor {
    fun findByCompanyId(companyId: String): Collection<TransmissionLineTemplate>
    fun findAllByCompanyIdAndMatrices3x3IsNotNull(companyId: String): Collection<TransmissionLineSingleCircuitTemplate>
    fun findAllByCompanyIdAndMatrices6x6IsNotNull(companyId: String): Collection<TransmissionLineDoubleCircuitTemplate>
}
