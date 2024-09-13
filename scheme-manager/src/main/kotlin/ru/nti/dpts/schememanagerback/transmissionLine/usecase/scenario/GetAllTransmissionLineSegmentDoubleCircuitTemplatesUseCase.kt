package ru.nti.dpts.schememanagerback.transmissionLine.usecase.scenario

import org.springframework.stereotype.Service
import ru.nti.dpts.schememanagerback.transmissionLine.domain.TransmissionLineDoubleCircuitTemplate
import ru.nti.dpts.schememanagerback.transmissionLine.domain.adapter.TransmissionLineTemplateExtractor
import ru.nti.dpts.schememanagerback.transmissionLine.usecase.GetAllTransmissionLineSegmentDoubleCircuitTemplates
import ru.nti.dpts.schememanagerback.user.service.UserService

//@Service
class GetAllTransmissionLineSegmentDoubleCircuitTemplatesUseCase(
    private val userService: UserService,
    private val transmissionLineTemplateExtractor: TransmissionLineTemplateExtractor
){

//    override fun execute(): List<TransmissionLineDoubleCircuitTemplate> {
//        return userService.getCurrentUser().let { user ->
//            transmissionLineTemplateExtractor
//                .findAllByCompanyIdAndMatrices6x6IsNotNull(user.companyId.toString())
//                .toList()
//        }
//    }
}
