package ru.nti.dpts.schememanagerback.scheme.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.nti.dpts.schememanagerback.application.UserIdFromRequest
import ru.nti.dpts.schememanagerback.application.common.rest.ok
import ru.nti.dpts.schememanagerback.application.common.rest.restBusinessError
import ru.nti.dpts.schememanagerback.scheme.controller.locker.SchemeEditorsHolder
import ru.nti.dpts.schememanagerback.scheme.controller.notifier.CommandBatchResponseDto
import ru.nti.dpts.schememanagerback.scheme.domain.CommandExecutionError
import ru.nti.dpts.schememanagerback.scheme.domain.command.BatchCommand
import ru.nti.dpts.schememanagerback.scheme.domain.command.CommandInvoker
import ru.nti.dpts.schememanagerback.scheme.service.MessageSourceService

@RestController
class CommandRestController(
//    private val commandInvoker: CommandInvoker,
    private val userIdFromRequest: UserIdFromRequest,
    private val messageSourceService: MessageSourceService,
    private val schemeEditorsHolder: SchemeEditorsHolder
) {

//    @PostMapping("/api/modeling/v3/scheme/{projectId}/command")
//    fun executeCommand(
//        @PathVariable projectId: String,
//        @RequestBody commandRequest: CommandBatchRequestDto
//    ): ResponseEntity<*> {
//        val userId = userIdFromRequest()
//        schemeEditorsHolder.updateProjectEditorOrThrowException(projectId, userId)
//
//        return commandRequest.toDomainCommand(projectId, userId)
//            .let { commandInvoker.invoke(it) }
//            .fold(
//                { it.toRestError() },
//                { ok(Unit) }
//            )
//    }
//
//    @PostMapping("/api/modeling/v3/scheme/{projectId}/undo")
//    fun undo(
//        @PathVariable projectId: String
//    ): ResponseEntity<*> {
//        val userId = userIdFromRequest()
//        schemeEditorsHolder.updateProjectEditorOrThrowException(projectId, userId)
//
//        return commandInvoker.undo(projectId, userIdFromRequest())
//            .fold(
//                { it.toRestError() },
//                {
//                    it as BatchCommand
//                    ok(CommandBatchResponseDto.from(it))
//                }
//            )
//    }
//
//    @PostMapping("/api/modeling/v3/scheme/{projectId}/redo")
//    fun redo(
//        @PathVariable projectId: String
//    ): ResponseEntity<*> {
//        val userId = userIdFromRequest()
//        schemeEditorsHolder.updateProjectEditorOrThrowException(projectId, userId)
//
//        return commandInvoker.redo(projectId, userIdFromRequest())
//            .fold(
//                { it.toRestError() },
//                {
//                    it as BatchCommand
//                    ok(CommandBatchResponseDto.from(it))
//                }
//            )
//    }
//
//    private fun CommandExecutionError.toRestError() =
//        when (this) {
//            is CommandExecutionError.LinkWithIdAlreadyPresented ->
//                restBusinessError(
//                    messageSourceService.getMessage("api.scheme.command.error.base-execution-error"),
//                    HttpStatus.BAD_REQUEST
//                )
//
//            is CommandExecutionError.EquipmentDosNotExistError ->
//                restBusinessError(
//                    messageSourceService.getMessage("api.scheme.command.error.base-execution-error"),
//                    HttpStatus.BAD_REQUEST
//                )
//
//            is CommandExecutionError.LinkDosNotExistError ->
//                restBusinessError(
//                    messageSourceService.getMessage("api.scheme.command.error.base-execution-error"),
//                    HttpStatus.BAD_REQUEST
//                )
//
//            is CommandExecutionError.EquipmentWithPresentedNameAlreadyExist ->
//                restBusinessError(
//                    messageSourceService.getMessage("api.scheme.error.equipment.twins"),
//                    HttpStatus.BAD_REQUEST
//                )
//
//            is CommandExecutionError.EquipmentFieldsValidationError ->
//                restBusinessError(
//                    validationError.convertToMessage(messageSourceService).message,
//                    HttpStatus.BAD_REQUEST
//                )
//        }
}
