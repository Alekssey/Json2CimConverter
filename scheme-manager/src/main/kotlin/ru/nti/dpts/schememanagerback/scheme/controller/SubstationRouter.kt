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
import ru.nti.dpts.schememanagerback.scheme.domain.Substation
import ru.nti.dpts.schememanagerback.scheme.service.MessageSourceService
import ru.nti.dpts.schememanagerback.scheme.usecases.scenarious.SubstationCreateDto
import ru.nti.dpts.schememanagerback.scheme.usecases.scenarious.SubstationManagerError
import ru.nti.dpts.schememanagerback.scheme.usecases.scenarious.SubstationService

@Controller
class SubstationRouter(
    private val substationService: SubstationService,
    private val simpMessageSendingOperations: SimpMessageSendingOperations,
    private val messageSourceService: MessageSourceService,
    private val schemeEditorsHolder: SchemeEditorsHolder
) {

//    @MessageMapping("/scheme.{projectId}.substation.create")
//    fun addSubstation(
//        @Payload command: CreateSubstationCommand,
//        @DestinationVariable projectId: String,
//        header: SimpMessageHeaderAccessor
//    ): Substation? {
//        val userId = header.user!!.name
//        schemeEditorsHolder.updateProjectEditorOrThrowException(projectId, userId)
//
//        return substationService.createSubstation(SubstationCreateDto(projectId, command.name))
//            .fold(
//                {
//                    simpMessageSendingOperations.convertAndSendToUser(
//                        header.user!!.name,
//                        "/queue/errors",
//                        it.retranslateError()
//                    )
//                    null
//                },
//                { it }
//            )
//    }

//    @MessageMapping("/scheme.{projectId}.substation.remove")
//    fun removeSubstation(
//        @Payload command: RemoveSubstationCommand,
//        @DestinationVariable projectId: String,
//        header: SimpMessageHeaderAccessor
//    ): Substation? {
//        val userId = header.user!!.name
//        schemeEditorsHolder.updateProjectEditorOrThrowException(projectId, userId)
//
//        return substationService.deleteSubstationIfEmpty(projectId, command.id)
//            .fold(
//                {
//                    simpMessageSendingOperations.convertAndSendToUser(
//                        header.user!!.name,
//                        "/queue/errors",
//                        it.retranslateError()
//                    )
//                    null
//                },
//                { it }
//            )
//    }

    @MessageExceptionHandler
    @SendToUser("/queue.errors", broadcast = false)
    fun handleException(exception: ProjectAlreadyEditByAnotherUserException): ErrorMessage {
        logger.error(exception.message, exception)
        return ErrorMessage(
            HttpStatus.CONFLICT,
            messageSourceService.getMessage("api.scheme.command.error.editor.conflict")
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    private fun SubstationManagerError.retranslateError(): ErrorMessage {
        return when (this) {
            is SubstationManagerError.SubstationDuplicationError ->
                ErrorMessage(
                    HttpStatus.BAD_REQUEST,
                    messageSourceService.getMessage("api.substation.error.twins")
                        .format(substationName)
                )

            is SubstationManagerError.SubstationNotFoundError ->
                ErrorMessage(
                    HttpStatus.BAD_REQUEST,
                    messageSourceService.getMessage("api.substation.error.notfound")
                )

            is SubstationManagerError.SubstationContainsEquipmentsError ->
                ErrorMessage(
                    HttpStatus.BAD_REQUEST,
                    messageSourceService.getMessage("api.substation.error.contains.elements")
                        .format(substationName)
                )

            is SubstationManagerError.UnknownError -> ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, null)
        }
    }
}

data class CreateSubstationCommand(val name: String)
data class RemoveSubstationCommand(val id: String)
