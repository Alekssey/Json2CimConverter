package ru.nti.dpts.schememanagerback.scheme.controller

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageExceptionHandler
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller
import ru.nti.dpts.schememanagerback.scheme.controller.locker.ProjectAlreadyEditByAnotherUserException
import ru.nti.dpts.schememanagerback.scheme.controller.locker.SchemeEditorsHolder
import ru.nti.dpts.schememanagerback.scheme.domain.TransmissionLine
import ru.nti.dpts.schememanagerback.scheme.service.MessageSourceService
import ru.nti.dpts.schememanagerback.scheme.usecases.scenarious.TransmissionLineCreateDto
import ru.nti.dpts.schememanagerback.scheme.usecases.scenarious.TransmissionLineManagerError
import ru.nti.dpts.schememanagerback.scheme.usecases.scenarious.TransmissionLineService

@Controller
class LineRoute(
    private val transmissionLineService: TransmissionLineService,
    private val simpMessageSendingOperations: SimpMessageSendingOperations,
    private val messageSourceService: MessageSourceService,
    private val schemeEditorsHolder: SchemeEditorsHolder
) {

//    @MessageMapping("/scheme.{projectId}.line.create")
//    fun addTransmissionLine(
//        @Payload command: CreateLineCommand,
//        @DestinationVariable projectId: String,
//        header: SimpMessageHeaderAccessor
//    ): TransmissionLine? {
//        val userId = header.user!!.name
//        schemeEditorsHolder.updateProjectEditorOrThrowException(projectId, userId)
//
//        return transmissionLineService.createTransmissionLine(
//            TransmissionLineCreateDto(
//                projectId,
//                command.name
//            )
//        )
//            .fold(
//                {
//                    simpMessageSendingOperations.convertAndSendToUser(
//                        header.user!!.name,
//                        "/queue/errors",
//                        it.translateError()
//                    )
//                    null
//                },
//                { it }
//            )
//    }

//    @MessageMapping("/scheme.{projectId}.line.remove")
//    fun removeTransmissionLine(
//        @Payload command: RemoveLineCommand,
//        @DestinationVariable projectId: String,
//        header: SimpMessageHeaderAccessor
//    ): TransmissionLine? {
//        val userId = header.user!!.name
//        schemeEditorsHolder.updateProjectEditorOrThrowException(projectId, userId)
//
//        return transmissionLineService.deleteTransmissionLine(projectId, command.id)
//            .fold(
//                {
//                    simpMessageSendingOperations.convertAndSendToUser(
//                        header.user!!.name,
//                        "/queue/errors",
//                        it.translateError()
//                    )
//                    null
//                },
//                { it }
//            )
//    }
//
//    @MessageExceptionHandler
//    @SendToUser("/queue.errors", broadcast = false)
//    fun handleException(exception: ProjectAlreadyEditByAnotherUserException): ErrorMessage {
//        logger.error(exception.message, exception)
//        return ErrorMessage(HttpStatus.CONFLICT, messageSourceService.getMessage("api.scheme.command.error.editor.conflict"))
//    }
//
//    companion object {
//        private val logger = LoggerFactory.getLogger(this::class.java)
//    }
//
//    private fun TransmissionLineManagerError.translateError(): ErrorMessage {
//        return when (this) {
//            is TransmissionLineManagerError.TransmissionLineDuplicationError ->
//                ErrorMessage(
//                    HttpStatus.BAD_REQUEST,
//                    messageSourceService.getMessage("api.transmissionline.error.twins")
//                        .format(transmissionLineName)
//                )
//
//            is TransmissionLineManagerError.TransmissionLineNotFoundError ->
//                ErrorMessage(
//                    HttpStatus.BAD_REQUEST,
//                    messageSourceService.getMessage("api.transmissionline.error.notfound")
//                )
//
//            is TransmissionLineManagerError.TransmissionLineContainsSegmentsError ->
//                ErrorMessage(
//                    HttpStatus.BAD_REQUEST,
//                    messageSourceService.getMessage("api.transmissionline.error.contains.elements")
//                        .format(transmissionLineName)
//                )
//
//            is TransmissionLineManagerError.UnknownError -> ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, null)
//        }
//    }
}

data class CreateLineCommand(val name: String)
data class RemoveLineCommand(val id: String)
