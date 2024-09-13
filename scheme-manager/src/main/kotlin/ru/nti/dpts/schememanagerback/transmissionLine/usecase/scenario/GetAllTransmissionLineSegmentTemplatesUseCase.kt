package ru.nti.dpts.schememanagerback.transmissionLine.usecase.scenario

import org.springframework.stereotype.Service
import ru.nti.dpts.schememanagerback.transmissionLine.domain.TransmissionLineSingleCircuitTemplate
import ru.nti.dpts.schememanagerback.transmissionLine.domain.adapter.TransmissionLineTemplateExtractor
import ru.nti.dpts.schememanagerback.transmissionLine.usecase.GetAllTransmissionLineSegmentTemplates
import ru.nti.dpts.schememanagerback.user.service.UserService

@Service
class GetAllTransmissionLineSegmentTemplatesUseCase(
    private val userService: UserService,
//    private val transmissionLineTemplateExtractor: TransmissionLineTemplateExtractor
)  {

//    override fun execute(): List<TransmissionLineSingleCircuitTemplate> {
//        return userService.getCurrentUser().let { user ->
//            transmissionLineTemplateExtractor
//                .findAllByCompanyIdAndMatrices3x3IsNotNull(user.companyId.toString())
//                .toList()
//        }
//    }
}
