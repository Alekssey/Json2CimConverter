package ru.nti.dpts.schememanagerback.scheme.domain.command

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentLinkDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.XyDto
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import ru.nti.dtps.proto.scheme.link.EquipmentLink

abstract class Command(
    val projectId: String,
    val userId: String
) {
    private var undoCommand: Command? = null

    open fun undo(): Command? = undoCommand

    open fun setUndoCommand(command: Command) {
        undoCommand = command
    }

    override fun toString(): String {
        return "Command(projectId='$projectId', userId='$userId', undoCommand=$undoCommand)"
    }
}

enum class CommandType {
    NODE_CREATE,
    NODE_COORDS_CHANGE,
    NODE_PORTS_CHANGE,
    NODE_DIMENSIONS_CHANGE,
    NODE_ROTATION_CHANGE,
    NODE_NAME_CHANGE,
    NODE_GROUP_CHANGE,
    NODE_UPDATE,
    NODE_DELETE,
    LINK_CREATE,
    LINK_UPDATE,
    LINK_DELETE
}

class BatchCommand(
    val id: String,
    projectId: String,
    userId: String,
    val commands: List<Command>

) : Command(projectId, userId) {
    override fun toString(): String {
        return "BatchCommand(id='$id', commands=$commands)"
    }
}

class CreateNodeCommand(
    val type: CommandType,
    projectId: String,
    userId: String,
    val body: CreateNode
) : Command(projectId, userId) {
    override fun toString(): String {
        return "CreateNodeCommand(type=$type, body=$body)"
    }
}

class ChangeNodeCoordsCommand(
    val type: CommandType,
    projectId: String,
    userId: String,
    val body: ChangeNodeCoords
) : Command(projectId, userId) {
    override fun toString(): String {
        return "ChangeNodeCoordsCommand(type=$type, body=$body)"
    }
}

class ChangeNodeDimensionsCommand(
    val type: CommandType,
    projectId: String,
    userId: String,
    val body: ChangeNodeDimensions
) : Command(projectId, userId) {
    override fun toString(): String {
        return "ChangeNodeDimensionsCommand(type=$type, body=$body)"
    }
}

class ChangeNodeRotationCommand(
    val type: CommandType,
    projectId: String,
    userId: String,
    val body: ChangeNodeRotation
) : Command(projectId, userId) {
    override fun toString(): String {
        return "ChangeNodeRotationCommand(type=$type, body=$body)"
    }
}

class ChangeNodeParamsCommand(
    val type: CommandType,
    projectId: String,
    userId: String,
    val body: ChangeNodeParams
) : Command(projectId, userId) {
    override fun toString(): String {
        return "ChangeNodeParamsCommand(type=$type, body=$body)"
    }
}

class ChangeNodePortsCommand(
    val type: CommandType,
    projectId: String,
    userId: String,
    val body: ChangeNodePorts
) : Command(projectId, userId) {
    override fun toString(): String {
        return "ChangeNodePortsCommand(type=$type, body=$body)"
    }
}

class DeleteNodeCommand(
    val type: CommandType,
    projectId: String,
    userId: String,
    val body: DeleteNode
) : Command(projectId, userId) {
    override fun toString(): String {
        return "DeleteNodeCommand(type=$type, body=$body)"
    }
}

class CreateLinkCommand(
    val type: CommandType,
    projectId: String,
    userId: String,
    val body: CreateLink
) : Command(projectId, userId) {
    override fun toString(): String {
        return "CreateLinkCommand(type=$type, body=$body)"
    }
}

class UpdateLinkCommand(
    val type: CommandType,
    projectId: String,
    userId: String,
    val body: UpdateLink
) : Command(projectId, userId) {
    override fun toString(): String {
        return "UpdateLinkCommand(type=$type, body=$body)"
    }
}

class DeleteLinkCommand(
    val type: CommandType,
    projectId: String,
    userId: String,
    val body: DeleteLink
) : Command(projectId, userId) {
    override fun toString(): String {
        return "DeleteLinkCommand(type=$type, body=$body)"
    }
}

data class CreateNode(
    val id: String,
    var voltageLevelId: VoltageLevelLibId?,
    val libEquipmentId: EquipmentLibId,
    val hour: Int = 0,
    val coords: XyDto,
    val dimensions: EquipmentNodeDomain.SizeDto,
    val fields: Map<FieldLibId, String?> = mapOf(),
    var controls: Map<ControlLibId, EquipmentNodeDomain.ControlDto> = mapOf(),
    val ports: List<EquipmentNodeDomain.PortDto> = listOf()
) {
    fun execute(): EquipmentNodeDomain {
        return EquipmentNodeDomain(
            id = this.id,
            voltageLevelId = this.voltageLevelId,
            libEquipmentId = this.libEquipmentId,
            hour = this.hour,
            dimensions = this.dimensions,
            fields = this.fields,
            controls = this.controls,
            ports = this.ports,
            coords = this.coords
        )
    }

    companion object {
        fun generateUndo(node: EquipmentNodeDomain): DeleteNode {
            return DeleteNode(
                node.id
            )
        }
    }
}

data class ChangeNodeCoords(
    val id: String,
    val coords: XyDto,
    val ports: List<EquipmentNodeDomain.PortDto>
) {
    fun execute(node: EquipmentNodeDomain): EquipmentNodeDomain {
        assert(node.id == this.id)
        return EquipmentNodeDomain(
            id = node.id,
            voltageLevelId = node.voltageLevelId,
            libEquipmentId = node.libEquipmentId,
            hour = node.hour,
            dimensions = node.dimensions,
            fields = node.fields,
            controls = node.controls,
            ports = this.ports,
            coords = this.coords
        )
    }

    companion object {
        fun generateUndo(node: EquipmentNodeDomain): ChangeNodeCoords {
            return ChangeNodeCoords(
                node.id,
                node.coords,
                node.ports
            )
        }
    }
}

data class ChangeNodeDimensions(
    val id: String,
    val ports: List<EquipmentNodeDomain.PortDto>,
    val coords: XyDto,
    val dimensions: EquipmentNodeDomain.SizeDto
) {
    fun execute(node: EquipmentNodeDomain): EquipmentNodeDomain {
        assert(node.id == this.id)
        return EquipmentNodeDomain(
            id = node.id,
            voltageLevelId = node.voltageLevelId,
            libEquipmentId = node.libEquipmentId,
            hour = node.hour,
            fields = node.fields,
            controls = node.controls,
            dimensions = this.dimensions,
            ports = this.ports,
            coords = this.coords
        )
    }

    companion object {
        fun generateUndo(node: EquipmentNodeDomain): ChangeNodeDimensions {
            return ChangeNodeDimensions(
                node.id,
                node.ports,
                node.coords,
                node.dimensions
            )
        }
    }
}

data class ChangeNodeRotation(
    val id: String,
    val coords: XyDto,
    val hour: Int
) {
    fun execute(node: EquipmentNodeDomain): EquipmentNodeDomain {
        assert(node.id == this.id)
        return EquipmentNodeDomain(
            id = node.id,
            voltageLevelId = node.voltageLevelId,
            libEquipmentId = node.libEquipmentId,
            fields = node.fields,
            controls = node.controls,
            dimensions = node.dimensions,
            ports = node.ports,
            hour = this.hour,
            coords = this.coords
        )
    }

    companion object {
        fun generateUndo(node: EquipmentNodeDomain): ChangeNodeRotation {
            return ChangeNodeRotation(
                node.id,
                node.coords,
                node.hour
            )
        }
    }
}

data class ChangeNodeParams(
    val id: String,
    var voltageLevelId: VoltageLevelLibId?,
    val fields: Map<FieldLibId, String?> = mapOf()
) {
    fun execute(node: EquipmentNodeDomain): EquipmentNodeDomain {
        assert(node.id == this.id)
        return EquipmentNodeDomain(
            id = node.id,
            voltageLevelId = this.voltageLevelId,
            libEquipmentId = node.libEquipmentId,
            fields = this.fields,
            controls = node.controls,
            dimensions = node.dimensions,
            ports = node.ports,
            hour = node.hour,
            coords = node.coords
        )
    }

    companion object {
        fun generateUndo(node: EquipmentNodeDomain): ChangeNodeParams {
            return ChangeNodeParams(
                node.id,
                node.voltageLevelId,
                node.fields
            )
        }
    }
}

data class ChangeNodePorts(
    val id: String,
    val ports: List<EquipmentNodeDomain.PortDto>
) {
    fun execute(node: EquipmentNodeDomain): EquipmentNodeDomain {
        assert(node.id == this.id)
        return EquipmentNodeDomain(
            id = node.id,
            voltageLevelId = node.voltageLevelId,
            libEquipmentId = node.libEquipmentId,
            fields = node.fields,
            controls = node.controls,
            dimensions = node.dimensions,
            hour = node.hour,
            coords = node.coords,
            ports = this.ports
        )
    }

    companion object {
        fun generateUndo(node: EquipmentNodeDomain): ChangeNodePorts {
            return ChangeNodePorts(
                node.id,
                node.ports
            )
        }
    }
}

data class DeleteNode(
    val id: String
) {
    companion object {
        fun generateUndo(node: EquipmentNodeDomain): CreateNode {
            return CreateNode(
                id = node.id,
                voltageLevelId = node.voltageLevelId,
                libEquipmentId = node.libEquipmentId,
                hour = node.hour,
                dimensions = node.dimensions,
                fields = node.fields,
                controls = node.controls,
                ports = node.ports,
                coords = node.coords
            )
        }
    }
}

data class CreateLink(
    val id: String,
    val alignmentType: EquipmentLink.AlignmentType = EquipmentLink.AlignmentType.RECTANGULAR,
    val locked: Boolean = false,
    val selected: Boolean = false,
    val source: String,
    val target: String,
    val sourcePort: String,
    val targetPort: String,
    val points: List<EquipmentLinkDomain.PointDto>
) {
    fun execute(): EquipmentLinkDomain {
        return EquipmentLinkDomain(
            id = this.id,
            alignmentType = this.alignmentType,
            locked = this.locked,
            selected = this.selected,
            source = this.source,
            target = this.target,
            sourcePort = this.sourcePort,
            targetPort = this.targetPort,
            points = this.points
        )
    }

    companion object {
        fun generateUndo(link: EquipmentLinkDomain): DeleteLink {
            return DeleteLink(
                link.id
            )
        }
    }
}

data class UpdateLink(
    val id: String,
    val points: List<EquipmentLinkDomain.PointDto>
) {
    fun execute(link: EquipmentLinkDomain): EquipmentLinkDomain {
        assert(link.id == id)
        return EquipmentLinkDomain(
            id = link.id,
            alignmentType = link.alignmentType,
            locked = link.locked,
            selected = link.selected,
            source = link.source,
            target = link.target,
            sourcePort = link.sourcePort,
            targetPort = link.targetPort,
            points = this.points
        )
    }

    companion object {
        fun generateUndo(link: EquipmentLinkDomain): UpdateLink {
            return UpdateLink(
                link.id,
                link.points
            )
        }
    }
}

data class DeleteLink(
    val id: String
) {
    companion object {
        fun generateUndo(link: EquipmentLinkDomain): CreateLink {
            return CreateLink(
                id = link.id,
                alignmentType = link.alignmentType,
                locked = link.locked,
                selected = link.selected,
                source = link.source,
                target = link.target,
                sourcePort = link.sourcePort,
                targetPort = link.targetPort,
                points = link.points
            )
        }
    }
}
