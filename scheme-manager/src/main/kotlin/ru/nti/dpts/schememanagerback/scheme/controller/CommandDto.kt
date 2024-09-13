package ru.nti.dpts.schememanagerback.scheme.controller

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import ru.nti.dpts.schememanagerback.scheme.domain.command.*

class CommandBatchRequestDto(
    val id: String,
    val commands: List<CommandDto>
) {
    fun toDomainCommand(projectId: String, userId: String): BatchCommand {
        return BatchCommand(
            id,
            projectId,
            userId,
            commands.map { it.toDomainCommand(projectId, userId) }
        )
    }
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = CreateNodeCommandDto::class, name = "NODE_CREATE"),
    JsonSubTypes.Type(value = ChangeNodeCoordsCommandDto::class, name = "NODE_COORDS_CHANGE"),
    JsonSubTypes.Type(value = ChangeNodeDimensionsCommandDto::class, name = "NODE_DIMENSIONS_CHANGE"),
    JsonSubTypes.Type(value = ChangeNodeRotationCommandDto::class, name = "NODE_ROTATION_CHANGE"),
    JsonSubTypes.Type(value = ChangeNodeParamsCommandDto::class, name = "NODE_NAME_CHANGE"),
    JsonSubTypes.Type(value = ChangeNodeParamsCommandDto::class, name = "NODE_UPDATE"),
    JsonSubTypes.Type(value = ChangeNodeParamsCommandDto::class, name = "NODE_GROUP_CHANGE"),
    JsonSubTypes.Type(value = ChangeNodePortsCommandDto::class, name = "NODE_PORTS_CHANGE"),
    JsonSubTypes.Type(value = DeleteNodeCommandDto::class, name = "NODE_DELETE"),
    JsonSubTypes.Type(value = CreateLinkCommandDto::class, name = "LINK_CREATE"),
    JsonSubTypes.Type(value = UpdateLinkCommandDto::class, name = "LINK_UPDATE"),
    JsonSubTypes.Type(value = DeleteLinkCommandDto::class, name = "LINK_DELETE")
)
abstract class CommandDto(val type: CommandType) {
    abstract fun toDomainCommand(projectId: String, userId: String): Command
}

class CreateNodeCommandDto(
    type: CommandType,
    val body: CreateNode
) : CommandDto(type) {
    override fun toDomainCommand(projectId: String, userId: String): Command {
        return CreateNodeCommand(type, projectId, userId, body)
    }
}

class ChangeNodeCoordsCommandDto(
    type: CommandType,
    val body: ChangeNodeCoords
) : CommandDto(type) {
    override fun toDomainCommand(projectId: String, userId: String): Command {
        return ChangeNodeCoordsCommand(type, projectId, userId, body)
    }
}

class ChangeNodeDimensionsCommandDto(
    type: CommandType,
    val body: ChangeNodeDimensions
) : CommandDto(type) {
    override fun toDomainCommand(projectId: String, userId: String): Command {
        return ChangeNodeDimensionsCommand(type, projectId, userId, body)
    }
}

class ChangeNodeRotationCommandDto(
    type: CommandType,
    val body: ChangeNodeRotation
) : CommandDto(type) {
    override fun toDomainCommand(projectId: String, userId: String): Command {
        return ChangeNodeRotationCommand(type, projectId, userId, body)
    }
}

class ChangeNodeParamsCommandDto(
    type: CommandType,
    val body: ChangeNodeParams
) : CommandDto(type) {
    override fun toDomainCommand(projectId: String, userId: String): Command {
        return ChangeNodeParamsCommand(type, projectId, userId, body)
    }
}

class ChangeNodePortsCommandDto(
    type: CommandType,
    val body: ChangeNodePorts
) : CommandDto(type) {
    override fun toDomainCommand(projectId: String, userId: String): Command {
        return ChangeNodePortsCommand(type, projectId, userId, body)
    }
}

class DeleteNodeCommandDto(
    type: CommandType,
    val body: DeleteNode
) : CommandDto(type) {
    override fun toDomainCommand(projectId: String, userId: String): Command {
        return DeleteNodeCommand(type, projectId, userId, body)
    }
}

class CreateLinkCommandDto(
    type: CommandType,
    val body: CreateLink
) : CommandDto(type) {
    override fun toDomainCommand(projectId: String, userId: String): Command {
        return CreateLinkCommand(type, projectId, userId, body)
    }
}

class UpdateLinkCommandDto(
    type: CommandType,
    val body: UpdateLink
) : CommandDto(type) {
    override fun toDomainCommand(projectId: String, userId: String): Command {
        return UpdateLinkCommand(type, projectId, userId, body)
    }
}

class DeleteLinkCommandDto(
    type: CommandType,
    val body: DeleteLink
) : CommandDto(type) {
    override fun toDomainCommand(projectId: String, userId: String): Command {
        return DeleteLinkCommand(type, projectId, userId, body)
    }
}
