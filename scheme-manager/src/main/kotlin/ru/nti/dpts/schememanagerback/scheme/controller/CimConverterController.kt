package ru.nti.dpts.schememanagerback.scheme.controller

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.nti.dpts.schememanagerback.application.UserIdFromRequest
import ru.nti.dpts.schememanagerback.application.common.rest.ok
import ru.nti.dpts.schememanagerback.application.common.rest.restBusinessError
import ru.nti.dpts.schememanagerback.scheme.domain.ProjectAggregate
import ru.nti.dpts.schememanagerback.scheme.service.MessageSourceService
import ru.nti.dpts.schememanagerback.scheme.service.converter.FileByteResource
import ru.nti.dpts.schememanagerback.scheme.usecases.CimSchemeConverter
import ru.nti.dpts.schememanagerback.scheme.usecases.CimSchemeConverterError
import ru.nti.dpts.schememanagerback.scheme.usecases.ProjectView
import ru.nti.dpts.schememanagerback.scheme.usecases.SchemeView
import ru.nti.dpts.schememanagerback.scheme.usecases.history.UnredoableSnapshot

@RestController
@RequestMapping("/api/modeling/v3/convert")
class CimConverterController(
//    private val cimSchemeConverter: CimSchemeConverter,
    private val messageSource: MessageSourceService,
    private val userIdFromRequest: UserIdFromRequest
) {

//    @PostMapping("/scheme/to/cim/{projectId}")
//    fun convertSchemeToCimAndZip(
//        @PathVariable projectId: String
//    ): ResponseEntity<*> {
//        return cimSchemeConverter
//            .toCim(projectId)
//            .fold(
//                { it.toRestError() },
//                { it.toZipResponse() }
//            )
//    }

//    @PostMapping("/cim/to/scheme/{projectId}")
//    fun convertCimToScheme(
//        @PathVariable projectId: String,
//        @RequestPart file: MultipartFile
//    ): ResponseEntity<*> {
//        return cimSchemeConverter
//            .fromCim(userIdFromRequest(), projectId, file)
//            .fold(
//                { it.toRestError() },
//                { ok(it.toView()) }
//            )
//    }

    private fun FileByteResource.toZipResponse() = ResponseEntity.ok()
        .contentType(MediaType.parseMediaType("application/zip"))
        .header(
            "Content-Disposition",
            "attachment; filename=\"${this.fileName}\""
        )
        .body(this.data)

//    private fun CimSchemeConverterError.toRestError() =
//        when (this) {
//            is CimSchemeConverterError.SchemeNotValidError ->
//                restBusinessError(
//                    messageSource.getMessage("api.cim.converter.precondition.error.validation"),
//                    HttpStatus.BAD_REQUEST
//                )
//
//            is CimSchemeConverterError.CimFileNotValidError ->
//                restBusinessError(
//                    messageSource.getMessage("api.cim.import.rdf.error"),
//                    HttpStatus.BAD_REQUEST
//                )
//
//            is CimSchemeConverterError.DataInCimFileNotValidError ->
//                restBusinessError(
//                    messageSource.getMessage("api.cim.import.validation.error"),
//                    HttpStatus.BAD_REQUEST
//                )
//
//            is CimSchemeConverterError.UnknownCimConverterError ->
//                restBusinessError(
//                    messageSource.getMessage("api.cim.import.internal.error"),
//                    HttpStatus.BAD_REQUEST
//                )
//        }

    private fun ProjectAggregate.toView() =
        ProjectView(
            this.id,
            this.name,
            this.date,
            this.valid,
            SchemeView.create(this.scheme),
            UnredoableSnapshot(false, false)
        )
}
