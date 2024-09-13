package ru.nti.dpts.schememanagerback.scheme.controller

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.nti.dpts.schememanagerback.application.common.rest.restBusinessError
import ru.nti.dpts.schememanagerback.scheme.service.MessageSourceService
import ru.nti.dpts.schememanagerback.scheme.service.converter.FileByteResource
import ru.nti.dpts.schememanagerback.scheme.usecases.ConvertSchemeToSsdCommand
import ru.nti.dpts.schememanagerback.scheme.usecases.SsdSchemeConverter
import ru.nti.dpts.schememanagerback.scheme.usecases.SsdSchemeConverterUseCaseError

@RestController
@RequestMapping("/api/modeling/v3/convert")
class SsdConverterController(
    private val messageSource: MessageSourceService,
//    private val ssdSchemeConverter: SsdSchemeConverter
) {

//    @PostMapping("/scheme/to/ssd/{projectId}")
//    fun convertSchemeToSsdAndZip(
//        @PathVariable projectId: String,
//        @RequestBody substationIds: Set<String>
//    ): ResponseEntity<*> {
//        return ConvertSchemeToSsdCommand(
//            projectId,
//            substationIds
//        ).let {
//            ssdSchemeConverter.handle(it)
//        }.fold(
//            { it.toRestError() },
//            { it.toZipResponse() }
//        )
//    }

    private fun FileByteResource.toZipResponse() = ResponseEntity.ok()
        .contentType(MediaType.parseMediaType("application/zip"))
        .header(
            "Content-Disposition",
            "attachment; filename=\"${this.fileName}\""
        )
        .body(this.data)

    private fun SsdSchemeConverterUseCaseError.toRestError() =
        when (this) {
            is SsdSchemeConverterUseCaseError.SchemeNotValidUseCaseUseCaseError ->
                restBusinessError(
                    messageSource.getMessage("api.ssd.converter.precondition.error.validation"),
                    HttpStatus.BAD_REQUEST
                )

            is SsdSchemeConverterUseCaseError.SchemeContainSinglePhaseElementUseCaseUseCaseError ->
                restBusinessError(
                    messageSource.getMessage("api.ssd.converter.error.contains.one.phase.elements"),
                    HttpStatus.BAD_REQUEST
                )
        }
}
