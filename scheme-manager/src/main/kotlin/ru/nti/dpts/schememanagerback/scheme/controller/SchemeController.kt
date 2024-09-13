package ru.nti.dpts.schememanagerback.scheme.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.nti.dpts.schememanagerback.application.UserIdFromRequest
import ru.nti.dpts.schememanagerback.application.common.rest.ok
import ru.nti.dpts.schememanagerback.application.common.rest.restError
import ru.nti.dpts.schememanagerback.scheme.controller.locker.SchemeEditorsHolder
import ru.nti.dpts.schememanagerback.scheme.domain.command.UpdateSchemeCommand
import ru.nti.dpts.schememanagerback.scheme.domain.command.ValidateSchemeCommand
import ru.nti.dpts.schememanagerback.scheme.service.MessageSourceService
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.*
import ru.nti.dpts.schememanagerback.scheme.usecases.*
import ru.nti.dpts.schememanagerback.scheme.usecases.scenarious.UpdateSchemeUseCase
import ru.nti.dtps.equipment.meta.info.dataclass.common.Language
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager

@RestController
//@RequestMapping("/api/modeling/v3")
class SchemeController(
//    private val getSchemeInfo: GetSchemeInfo,
//    private val getProjectView: GetProjectView,
//    private val userIdFromRequest: UserIdFromRequest,
//    private val updateScheme: UpdateSchemeUseCase,
//    private val validateScheme: ValidateScheme,
//    private val messageSource: MessageSourceService,
//    private val schemeEditorsHolder: SchemeEditorsHolder
) {

//    @GetMapping("/scheme/{projectId}")
//    fun getProjectView(
//        @PathVariable projectId: String
//    ): ProjectView {
//        return getProjectView.execute(projectId, userIdFromRequest())
//    }

//    @GetMapping("/scheme/info/{projectId}")
//    fun getSchemeInfo(
//        @PathVariable projectId: String
//    ): SchemeInfo {
//        return getSchemeInfo.execute(projectId)
//    }

//    @PostMapping("/scheme/{projectId}")
//    fun updateScheme(
//        @PathVariable projectId: String,
//        @RequestBody scheme: SchemeView
//    ) {
//        val userId = userIdFromRequest()
//        schemeEditorsHolder.updateProjectEditorOrThrowException(projectId, userId)
//
//        updateScheme.execute(UpdateSchemeCommand(projectId, userId, scheme.toDomain()))
//    }

//    @PatchMapping("/scheme/validation/{projectId}")
//    fun validateScheme(
//        @PathVariable projectId: String
//    ): ResponseEntity<*> {
//        val userId = userIdFromRequest()
//        schemeEditorsHolder.updateProjectEditorOrThrowException(projectId, userId)
//
//        return validateScheme.execute(ValidateSchemeCommand(projectId, userId))
//            .fold(
//                {
//                    restError(
//                        it.convertToMessage(messageSource),
//                        HttpStatus.BAD_REQUEST
//                    )
//                },
//                { ok(Unit) }
//            )
//    }
}

data class ValidationRestError(
    val message: String,
    val equipmentId: String? = null
)

fun ValidationError.convertToMessage(messageSource: MessageSourceService): ValidationRestError {
    return when (this) {
        is EquipmentCountIsMoreThenLimitError -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.equipments.number")
                .format(currentEquipmentNumber, allowedEquipmentNumber)
        )

        is SchemeContainsIslandsError -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.islands")
        )

        is SchemeConnectionValidationError.SchemeContainsBlankPort -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.equipments.port.not.connected").format(equipmentName),
            equipmentId
        )

        is SchemeConnectionValidationError.SchemeContainsDisconnectedLinks -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.link.not.connected")
        )

        is SchemeContainsLinksWithInvalidPhasesConnection -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.phases.type"),
            equipmentId
        )

        is SchemeDoesNotContainsRequiredEquipmentError -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.equipments.notfound")
                .format(
                    requiredEquipment
                        .map { EquipmentMetaInfoManager.getEquipmentLibById(it).name[Language.RU] }
                        .joinToString(separator = "\", \"")
                )
        )

        is EquipmentParamsValidationError.FieldValueLessThenLimitValueError -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.field.value.min.bound")
                .format(this.equipmentName, fieldLib.name[Language.RU], this.boundValue),
            equipmentId
        )

        is EquipmentParamsValidationError.FieldValueMoreThenLimitValueError -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.field.value.max.bound")
                .format(this.equipmentName, fieldLib.name[Language.RU], this.boundValue),
            equipmentId
        )

        is EquipmentParamsValidationError.TextFieldLengthDoNotEqualValueError -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.field.value.length")
                .format(this.equipmentName, fieldLib.name[Language.RU], this.boundValue),
            equipmentId
        )

        is EquipmentParamsValidationError.TextFieldLengthMoreThenMaxValueError -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.field.value.max.length")
                .format(this.equipmentName, fieldLib.name[Language.RU], this.boundValue),
            equipmentId
        )

        is EquipmentParamsValidationError.TextFieldLessThenMinValueError -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.field.value.min.length")
                .format(this.equipmentName, fieldLib.name[Language.RU], this.boundValue),
            equipmentId
        )

        is EquipmentParamsValidationError.RequiredFieldNotFoundError -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.field.value.notfound")
                .format(equipmentName, fieldLib.name[Language.RU]),
            equipmentId
        )

        is EquipmentParamsValidationError.EquipmentDoesNotHaveNameError -> ValidationRestError(
            messageSource.getMessage("api.scheme.error.equipment.name"),
            equipmentId
        )

        is EquipmentParamsValidationError.GeneratorPreconditionParamsError -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.field.value.synchronousgenerator")
                .format(equipmentName),
            equipmentId
        )

        is EquipmentParamsValidationError.LineSegmentPreconditionConcentratedParamsError -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.field.value.concentrated.parameters")
                .format(equipmentName),
            equipmentId
        )

        is EquipmentParamsValidationError.LineSegmentPreconditionParamsError -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.field.value.transmissionline")
                .format(equipmentName),
            equipmentId
        )

        is EquipmentParamsValidationError.PowerTransformerRpnValuesError -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.field.value.transformer.rpn")
                .format(equipmentName),
            equipmentId
        )

        is EquipmentParamsValidationError.PowerTransformerMagneticCouplingError -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.field.value.transformer.magnetic.coupling").format(
                equipmentName
            ),
            equipmentId
        )

        is EquipmentParamsValidationError.LineTemplateNotSelected -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.field.value.line.template").format(
                equipmentName
            ),
            equipmentId
        )

        is TransmissionLineValidationError.TransmissionLineHasSegmentWithDifferentVoltages -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.transmissionline.voltages")
                .format(transmissionLineName)
        )

        is TransmissionLineValidationError.TransmissionLineSegmentsDoesNotConnectedError -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.transmissionline.islands")
                .format(transmissionLineName)
        )

        is VoltageValidationError.ConnectedNodesHaveDifferentVoltageLevels -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.nodes.voltages").format(
                equipmentSourceName,
                equipmentTargetName,
                sourceEquipmentVoltage,
                targetEquipmentVoltage
            ),
            equipmentId
        )

        else -> ValidationRestError(
            messageSource.getMessage("api.scheme.validation.error.base")
        )
    }
}
