package ru.nti.dpts.schememanagerback.scheme.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import ru.nti.dpts.schememanagerback.application.common.base.AggregateRoot
import ru.nti.dpts.schememanagerback.scheme.domain.command.*
import ru.nti.dpts.schememanagerback.scheme.domain.events.SchemeEditorCommandExecutedEvent
import ru.nti.dpts.schememanagerback.scheme.domain.events.SchemeUpdatedEvent
import ru.nti.dpts.schememanagerback.scheme.domain.events.SchemeValidatedEvent
import ru.nti.dpts.schememanagerback.scheme.service.augmentation.AugmentationService
import ru.nti.dpts.schememanagerback.scheme.service.validator.ComplexSchemeValidator
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.EquipmentParamsValidationError
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.ValidationError
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import java.util.*

class ProjectAggregate internal constructor(
    id: String,
    var name: String,
    val date: Date,
    var valid: Boolean = false,
    var scheme: SchemeDomain
) : AggregateRoot<String>(id) {

    companion object {
        fun create(projectId: String, name: String, date: Date): ProjectAggregate {
            return ProjectAggregate(
                projectId,
                name,
                date,
                false,
                SchemeDomain.create()
            )
        }
    }

    fun update(name: String) {
        this.name = name
    }

    /*
    * Do not generate event cause cim converter flow initially set coordinate
    * */
    fun updateSchemeWhileCimConverter(
        unredoableHistoryCleaner: UnredoableHistoryCleaner,
        schemeFromCim: SchemeDomain
    ) {
        scheme = schemeFromCim
        valid = false
        scheme.version = 0
        unredoableHistoryCleaner.clear(id)
    }

    fun validate(
        complexSchemeValidator: ComplexSchemeValidator,
        augmentationService: AugmentationService
    ): Either<ValidationError, Unit> {
        augmentationService.augment(scheme)
        return complexSchemeValidator.validate(scheme).fold(
            { it.left() },
            {
                setProjectValidAndUpSchemeVersion()
                addEvent(
                    SchemeValidatedEvent(
                        this.id,
                        this.scheme.version
                    )
                )
                Unit.right()
            }
        )
    }

    fun updateSchemeFromInternal(
        scheme: SchemeDomain,
        userId: String,
        unredoableHistoryCleaner: UnredoableHistoryCleaner
    ) {
        updateScheme(scheme, userId, unredoableHistoryCleaner)
    }

    private fun updateScheme(scheme: SchemeDomain, userId: String, unredoableHistoryCleaner: UnredoableHistoryCleaner) {
        this.scheme = scheme
        this.valid = false
        unredoableHistoryCleaner.clear(this.id)
        addEvent(SchemeUpdatedEvent(this.id, userId, this.scheme.version))
    }

    private fun setProjectValidAndUpSchemeVersion() {
        this.valid = true
        this.scheme.version += 1
    }

    fun execute(command: Command): Either<CommandExecutionError, Unit> {
        if (valid) {
            valid = false
        }
        return when (command) {
            is BatchCommand -> handle(command)
            is CreateNodeCommand -> handle(command)
            is ChangeNodeCoordsCommand -> handle(command)
            is ChangeNodeDimensionsCommand -> handle(command)
            is ChangeNodeRotationCommand -> handle(command)
            is ChangeNodeParamsCommand -> handle(command)
            is ChangeNodePortsCommand -> handle(command)
            is DeleteNodeCommand -> handle(command)

            is DeleteLinkCommand -> handle(command)
            is CreateLinkCommand -> handle(command)
            is UpdateLinkCommand -> handle(command)

            else -> error("Unimplemented command type ")
        }
    }

    private fun handle(command: CreateNodeCommand): Either<CommandExecutionError, Unit> {
        return scheme.execute(command.body)
            .fold(
                {
                    return when (it) {
                        is NodeCreateError.NameDuplicationError ->
                            CommandExecutionError.EquipmentWithPresentedNameAlreadyExist(it.name).left()
                        is NodeCreateError.FieldValidationError ->
                            CommandExecutionError.EquipmentFieldsValidationError(it.validationError).left()
                    }
                },
                {
                    command.setUndoCommand(
                        DeleteNodeCommand(
                            CommandType.NODE_DELETE,
                            command.projectId,
                            command.userId,
                            it
                        )
                    )
                    Unit.right()
                }
            )
    }

    private fun handle(command: ChangeNodeCoordsCommand): Either<CommandExecutionError, Unit> {
        return scheme.execute(command.body)
            .fold(
                { error -> CommandExecutionError.EquipmentDosNotExistError(error.nodeId).left() },
                {
                    command
                        .setUndoCommand(
                            ChangeNodeCoordsCommand(
                                command.type,
                                command.projectId,
                                command.userId,
                                it
                            )
                        )
                    Unit.right()
                }
            )
    }

    private fun handle(command: ChangeNodeDimensionsCommand): Either<CommandExecutionError, Unit> {
        return scheme.execute(command.body)
            .fold(
                { error -> CommandExecutionError.EquipmentDosNotExistError(error.nodeId).left() },
                {
                    command
                        .setUndoCommand(
                            ChangeNodeDimensionsCommand(
                                command.type,
                                command.projectId,
                                command.userId,
                                it
                            )
                        )
                    Unit.right()
                }
            )
    }

    private fun handle(command: ChangeNodeRotationCommand): Either<CommandExecutionError, Unit> {
        return scheme.execute(command.body)
            .fold(
                { error -> CommandExecutionError.EquipmentDosNotExistError(error.nodeId).left() },
                {
                    command
                        .setUndoCommand(
                            ChangeNodeRotationCommand(
                                command.type,
                                command.projectId,
                                command.userId,
                                it
                            )
                        )
                    Unit.right()
                }
            )
    }

    private fun handle(command: ChangeNodeParamsCommand): Either<CommandExecutionError, Unit> {
        return scheme.execute(command.body)
            .fold(
                {
                    return when (it) {
                        is NodeUpdateParamsError.NameDuplicationError ->
                            CommandExecutionError.EquipmentWithPresentedNameAlreadyExist(it.name).left()

                        is NodeUpdateParamsError.NodeNotFoundError ->
                            CommandExecutionError.EquipmentDosNotExistError(it.nodeId).left()

                        is NodeUpdateParamsError.FieldValidationError ->
                            CommandExecutionError.EquipmentFieldsValidationError(it.validationError).left()
                    }
                },
                {
                    command
                        .setUndoCommand(
                            ChangeNodeParamsCommand(
                                command.type,
                                command.projectId,
                                command.userId,
                                it
                            )
                        )
                    Unit.right()
                }
            )
    }

    private fun handle(command: ChangeNodePortsCommand): Either<CommandExecutionError, Unit> {
        return scheme.execute(command.body)
            .fold(
                { error -> CommandExecutionError.EquipmentDosNotExistError(error.nodeId).left() },
                {
                    command
                        .setUndoCommand(
                            ChangeNodePortsCommand(
                                command.type,
                                command.projectId,
                                command.userId,
                                it
                            )
                        )
                    Unit.right()
                }
            )
    }

    private fun handle(command: DeleteNodeCommand): Either<CommandExecutionError, Unit> {
        return scheme.execute(command.body)
            .fold(
                { error -> CommandExecutionError.EquipmentDosNotExistError(error.nodeId).left() },
                {
                    command.setUndoCommand(
                        CreateNodeCommand(
                            CommandType.NODE_CREATE,
                            command.projectId,
                            command.userId,
                            it
                        )
                    )
                    Unit.right()
                }
            )
    }

    private fun handle(command: CreateLinkCommand): Either<CommandExecutionError, Unit> {
        return scheme.execute(command.body)
            .fold(
                { error -> CommandExecutionError.LinkWithIdAlreadyPresented(error.id).left() },
                {
                    command.setUndoCommand(
                        DeleteLinkCommand(
                            CommandType.LINK_DELETE,
                            command.projectId,
                            command.userId,
                            it
                        )
                    )
                    Unit.right()
                }
            )
    }

    private fun handle(command: UpdateLinkCommand): Either<CommandExecutionError, Unit> {
        return scheme.execute(command.body)
            .fold(
                { error -> CommandExecutionError.LinkDosNotExistError(error.linkId).left() },
                {
                    command.setUndoCommand(
                        UpdateLinkCommand(
                            command.type,
                            command.projectId,
                            command.userId,
                            it
                        )
                    )
                    Unit.right()
                }
            )
    }

    private fun handle(command: DeleteLinkCommand): Either<CommandExecutionError, Unit> {
        return scheme.execute(command.body)
            .fold(
                { error -> CommandExecutionError.LinkDosNotExistError(error.linkId).left() },
                {
                    command.setUndoCommand(
                        CreateLinkCommand(
                            CommandType.LINK_CREATE,
                            command.projectId,
                            command.userId,
                            it
                        )
                    )
                    Unit.right()
                }
            )
    }

    private fun handle(command: BatchCommand): Either<CommandExecutionError, Unit> {
        command.commands.forEach { innerCommand ->
            this.execute(innerCommand).fold(
                { return it.left() },
                { Unit.right() }
            )
        }
        command.commands.mapNotNull { it.undo() }.apply {
            if (this.isNotEmpty()) {
                command.setUndoCommand(BatchCommand(command.id, command.projectId, command.userId, this))
            }
        }
        addEvent(
            SchemeEditorCommandExecutedEvent(
                command.projectId,
                command.userId,
                command
            )
        )
        return Unit.right()
    }
}

sealed class CommandExecutionError {
    class LinkWithIdAlreadyPresented(val id: String) : CommandExecutionError()
    class LinkDosNotExistError(val id: String) : CommandExecutionError()
    class EquipmentDosNotExistError(val id: String) : CommandExecutionError()
    class EquipmentWithPresentedNameAlreadyExist(val name: String) : CommandExecutionError()

    class EquipmentFieldsValidationError(val validationError: EquipmentParamsValidationError) : CommandExecutionError()
}

private val dashboardTypesNode =
    setOf(EquipmentLibId.BUTTON, EquipmentLibId.VALUE, EquipmentLibId.MEASUREMENT, EquipmentLibId.INDICATOR)

fun EquipmentLibId.isDashboardTypeLib() =
    dashboardTypesNode.contains(this)
