package ru.nti.dpts.schememanagerback.transmissionLine.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.nti.dpts.schememanagerback.transmissionLine.domain.Matrices3x3
import ru.nti.dpts.schememanagerback.transmissionLine.domain.Matrices6x6
import ru.nti.dpts.schememanagerback.transmissionLine.domain.TransmissionLineDoubleCircuitTemplate
import ru.nti.dpts.schememanagerback.transmissionLine.domain.TransmissionLineSingleCircuitTemplate
import ru.nti.dpts.schememanagerback.transmissionLine.usecase.GetAllTransmissionLineSegmentDoubleCircuitTemplates
import ru.nti.dpts.schememanagerback.transmissionLine.usecase.GetAllTransmissionLineSegmentTemplates
import java.util.*

//@RestController
//@RequestMapping("/api/modeling/v1/line-template")
class TransmissionLineTemplateController(
//    private val getAllTransmissionLineSegmentTemplates: GetAllTransmissionLineSegmentTemplates,
//    private val getAllTransmissionLineSegmentDoubleCircuitTemplates: GetAllTransmissionLineSegmentDoubleCircuitTemplates
) {

//    @GetMapping("/single-circuit")
//    fun getAllTransmissionLineSegmentTemplates(): List<SingleCircuitTemplateResponse> {
//        return getAllTransmissionLineSegmentTemplates.execute()
//            .map { it.toSingleCircuitResponse() }
//    }
//
//    @GetMapping("/double-circuit")
//    fun getAllTransmissionLineSegmentDoubleCircuitTemplates(): List<DoubleCircuitTemplateResponse> {
//        return getAllTransmissionLineSegmentDoubleCircuitTemplates.execute()
//            .map { it.toDoubleCircuitResponse() }
//    }
}

private fun TransmissionLineSingleCircuitTemplate.toSingleCircuitResponse() = SingleCircuitTemplateResponse(
    id = id,
    name = name,
    frequency = frequency,
    lastModifiedAt = lastModifiedAt,
    matrices3x3 = matrices3x3
)

private fun TransmissionLineDoubleCircuitTemplate.toDoubleCircuitResponse() = DoubleCircuitTemplateResponse(
    id = id,
    name = name,
    frequency = frequency,
    lastModifiedAt = lastModifiedAt,
    matrices6x6 = matrices6x6
)

data class SingleCircuitTemplateResponse(
    val id: UUID,
    val name: String,
    val frequency: Int,
    val lastModifiedAt: Long,
    val matrices3x3: Matrices3x3
)

data class DoubleCircuitTemplateResponse(
    val id: UUID,
    val name: String,
    val frequency: Int,
    val lastModifiedAt: Long,
    val matrices6x6: Matrices6x6
)
