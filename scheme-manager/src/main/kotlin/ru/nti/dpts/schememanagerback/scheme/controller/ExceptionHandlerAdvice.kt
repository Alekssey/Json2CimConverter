package ru.nti.dpts.schememanagerback.scheme.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.nti.dpts.schememanagerback.application.common.rest.BusinessError
import ru.nti.dpts.schememanagerback.scheme.controller.locker.ProjectAlreadyEditByAnotherUserException
import ru.nti.dpts.schememanagerback.scheme.domain.exception.ProjectDoesNotExistsException
import ru.nti.dpts.schememanagerback.scheme.service.MessageSourceService
import ru.nti.dpts.schememanagerback.scheme.service.converter.SsdConverterException
import javax.validation.ConstraintViolationException

@RestControllerAdvice
class ExceptionHandlerAdvice(
    private val objectMapper: ObjectMapper,
    private val messageSourceService: MessageSourceService
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SsdConverterException::class)
    fun handle(exception: SsdConverterException): JsonNode {
        log.error(exception.message, exception)
        return objectMapper.createObjectNode().put("message", exception.message)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProjectDoesNotExistsException::class)
    fun handle(exception: ProjectDoesNotExistsException): BusinessError {
        log.error(" Required project {} not found", exception.projectId)
        return BusinessError(messageSourceService.getMessage("api.project.error.notfound"))
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException::class)
    fun handle(exception: IllegalArgumentException): JsonNode {
        log.error(exception.message, exception)
        return objectMapper.createObjectNode().put("message", exception.message)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException::class)
    fun handle(exception: ConstraintViolationException): String? {
        return exception.constraintViolations.joinToString("\n") { it.message }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ProjectAlreadyEditByAnotherUserException::class)
    fun handle(exception: ProjectAlreadyEditByAnotherUserException): BusinessError {
        return BusinessError(
            messageSourceService.getMessage("api.scheme.command.error.editor.conflict")
        )
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(NotImplementedError::class)
    fun handle(exception: NotImplementedError): String {
        log.error(exception.message, exception)
        return messageSourceService.getMessage("api.not.implemented.error")
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handle(exception: Exception): String {
        log.error(exception.message, exception)
        return "Internal server error"
    }
}
