package ru.nti.dpts.schememanagerback.scheme.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import ru.nti.dpts.schememanagerback.application.common.error.BusinessError
import ru.nti.dpts.schememanagerback.meta.migration.ChangeSetsData
import ru.nti.dpts.schememanagerback.scheme.domain.command.*
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getName
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getSubstationId
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getTransmissionLineIds
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.isEquipmentWithName
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.EquipmentParamsValidationError
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.RequiredFieldsValidator
import ru.nti.dtps.equipment.meta.info.extension.isTransmissionLine
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

class SchemeDomain(
    val locked: Boolean = false,
    val zoom: Double = 0.0,
    val offsetX: Double = 0.0,
    val offsetY: Double = 0.0,
    var version: Long = 0L,
    val metaSchemeChangeSetId: Int = 0,
    val metaSchemeVersion: String = "",
    nodes: Map<String, EquipmentNodeDomain> = mapOf(),
    links: Map<String, EquipmentLinkDomain> = mapOf(),
    substations: List<Substation> = listOf(),
    transmissionLines: List<TransmissionLine> = listOf()
) {
    val nodes = nodes.filter { !it.value.libEquipmentId.isDashboardTypeLib() }.toMutableMap()
    val dashboardNodes = nodes.filter { it.value.libEquipmentId.isDashboardTypeLib() }.toMutableMap()
    val links: MutableMap<String, EquipmentLinkDomain> = links.toMutableMap()
    val substations: MutableList<Substation> = substations.toMutableList()
    val transmissionLines: MutableList<TransmissionLine> = transmissionLines.toMutableList()

    companion object {
        fun create(): SchemeDomain {
            return SchemeDomain(
                zoom = 1.0,
                metaSchemeChangeSetId = ChangeSetsData.actualChangeSetId,
                metaSchemeVersion = ChangeSetsData.relatedMetaSchemeVersion
            )
        }
    }

    fun execute(change: CreateNode): Either<NodeCreateError, DeleteNode> {
        if (change.libEquipmentId.isEquipmentWithName()) {
            val name = change.fields[FieldLibId.NAME]!!
            if (!isNodeNameUnique(name)) {
                return NodeCreateError.NameDuplicationError(name).left()
            }
        }

        val createdNode = change.execute()
        validateUpdatedNode(createdNode).mapLeft { return NodeCreateError.FieldValidationError(it).left() }

        if (change.libEquipmentId.isDashboardTypeLib()) {
            dashboardNodes[createdNode.id] = createdNode
        } else {
            nodes[createdNode.id] = createdNode
        }
        return CreateNode.generateUndo(createdNode).right()
    }

    fun execute(change: ChangeNodeCoords): Either<NodeNotFoundError, ChangeNodeCoords> {
        return if (nodes.contains(change.id)) {
            changeNodeCoords(change, nodes)
        } else {
            changeNodeCoords(change, dashboardNodes)
        }
    }

    private fun changeNodeCoords(
        change: ChangeNodeCoords,
        affectedNodes: MutableMap<String, EquipmentNodeDomain>
    ): Either<NodeNotFoundError, ChangeNodeCoords> {
        return affectedNodes[change.id]?.let { node ->
            val undo = ChangeNodeCoords.generateUndo(node)
            affectedNodes[change.id] = change.execute(node)
            undo.right()
        } ?: NodeNotFoundError(change.id).left()
    }

    fun execute(change: ChangeNodeRotation): Either<NodeNotFoundError, ChangeNodeRotation> {
        return nodes[change.id]?.let { node ->
            val undo = ChangeNodeRotation.generateUndo(node)
            nodes[change.id] = change.execute(node)
            undo.right()
        } ?: NodeNotFoundError(change.id).left()
    }

    fun execute(change: ChangeNodeDimensions): Either<NodeNotFoundError, ChangeNodeDimensions> {
        return nodes[change.id]?.let { node ->
            val undo = ChangeNodeDimensions.generateUndo(node)
            nodes[change.id] = change.execute(node)
            undo.right()
        } ?: NodeNotFoundError(change.id).left()
    }

    fun execute(change: ChangeNodeParams): Either<NodeUpdateParamsError, ChangeNodeParams> {
        val node = (nodes + dashboardNodes)[change.id]
            ?: return NodeUpdateParamsError.NodeNotFoundError(change.id).left()

        if (node.libEquipmentId.isEquipmentWithName()) {
            val name = change.fields[FieldLibId.NAME]!!
            if (!isNodeNameUniqueExcludedNode(name, change.id)) {
                NodeUpdateParamsError.NameDuplicationError(name).left()
            }
        }

        return if (nodes.contains(change.id)) {
            executeNodeChangeAndUpdate(nodes[change.id]!!, change, nodes)
        } else {
            executeNodeChangeAndUpdate(dashboardNodes[change.id]!!, change, dashboardNodes)
        }
    }

    private fun executeNodeChangeAndUpdate(
        node: EquipmentNodeDomain,
        change: ChangeNodeParams,
        affectedNodes: MutableMap<String, EquipmentNodeDomain>
    ): Either<NodeUpdateParamsError, ChangeNodeParams> {
        val undo = ChangeNodeParams.generateUndo(node)
        val updatedNode = change.execute(node)
        return validateUpdatedNode(updatedNode).fold(
            { NodeUpdateParamsError.FieldValidationError(it).left() },
            {
                affectedNodes[change.id] = updatedNode
                undo.right()
            }
        )
    }

    private fun validateUpdatedNode(updatedNode: EquipmentNodeDomain): Either<EquipmentParamsValidationError, Unit> {
        return RequiredFieldsValidator.validateFields(updatedNode)
    }

    fun execute(change: ChangeNodePorts): Either<NodeNotFoundError, ChangeNodePorts> {
        return nodes[change.id]?.let { node ->
            val undo = ChangeNodePorts.generateUndo(node)
            nodes[change.id] = change.execute(node)
            undo.right()
        } ?: NodeNotFoundError(change.id).left()
    }

    fun execute(change: DeleteNode): Either<NodeNotFoundError, CreateNode> {
        return nodes.remove(change.id)?.let {
            DeleteNode.generateUndo(it).right()
        } ?: dashboardNodes.remove(change.id)?.let {
            DeleteNode.generateUndo(it).right()
        } ?: NodeNotFoundError(change.id).left()
    }

    private fun isNodeNameUnique(nodeName: String): Boolean {
        return (nodes.values + dashboardNodes.values)
            .filter { it.libEquipmentId.isEquipmentWithName() }
            .firstOrNull { it.getName() == nodeName } == null
    }

    private fun isNodeNameUniqueExcludedNode(nodeName: String, excludedId: String): Boolean {
        return (nodes.values + dashboardNodes.values)
            .filter { it.id != excludedId && it.libEquipmentId.isEquipmentWithName() }
            .firstOrNull { it.getName() == nodeName } == null
    }

    fun execute(change: CreateLink): Either<IdDuplicationError, DeleteLink> {
        return links[change.id]?.let {
            IdDuplicationError(change.id).left()
        }
            ?: change.execute()
                .apply {
                    links[this.id] = this
                }.let { CreateLink.generateUndo(it).right() }
    }

    fun execute(change: UpdateLink): Either<LinkNotFoundError, UpdateLink> {
        return links[change.id]?.let { link ->
            val undo = UpdateLink.generateUndo(link)
            links[change.id] = change.execute(link)
            undo.right()
        } ?: LinkNotFoundError(change.id).left()
    }

    fun execute(change: DeleteLink): Either<LinkNotFoundError, CreateLink> {
        return links.remove(change.id)?.let {
            DeleteLink.generateUndo(it).right()
        } ?: LinkNotFoundError(change.id).left()
    }

    fun createTransmissionLine(name: String): Either<TransmissionLineDuplicationError, TransmissionLine> {
        transmissionLines.find { it.name == name }?.let {
            return TransmissionLineDuplicationError(name).left()
        }
        return TransmissionLine.create(name)
            .also { transmissionLines.add(it) }
            .right()
    }

    fun removeTransmissionLine(id: String): Either<BusinessError, TransmissionLine> {
        return transmissionLines.find { it.id == id }?.let { transmissionLine ->
            val transmissionLineSegments = nodes.values.filter {
                it.libEquipmentId.isTransmissionLine() ||
                    it.libEquipmentId == EquipmentLibId.TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT
            }

            if (transmissionLineSegments.any { it.getTransmissionLineIds().contains(id) }) {
                TransmissionLineContainsSegmentsError(transmissionLine.name).left()
            } else {
                transmissionLines.remove(transmissionLine)
                transmissionLine.right()
            }
        } ?: return TransmissionLineNotFoundError().left()
    }

    fun createSubstation(name: String): Either<SubstationDuplicationError, Substation> {
        substations.find { it.name == name }?.let {
            return SubstationDuplicationError(name).left()
        }
        return Substation.create(name)
            .also { substations.add(it) }
            .right()
    }

    fun removeSubstation(id: String): Either<BusinessError, Substation> {
        return substations.find { it.id == id }?.let { substation ->
            if (nodes.values.any { it.getSubstationId() == id }) {
                SubstationContainsEquipmentsError(substation.name).left()
            } else {
                substations.remove(substation)
                substation.right()
            }
        } ?: return SubstationNotFoundError().left()
    }
}

class IdDuplicationError(
    val id: String
) : BusinessError

sealed class NodeCreateError : BusinessError {
    class NameDuplicationError(
        val name: String
    ) : NodeCreateError()

    class FieldValidationError(
        val validationError: EquipmentParamsValidationError
    ) : NodeCreateError()
}
sealed class NodeUpdateParamsError : BusinessError {
    class FieldValidationError(
        val validationError: EquipmentParamsValidationError
    ) : NodeUpdateParamsError()

    class NameDuplicationError(
        val name: String
    ) : NodeUpdateParamsError()

    class NodeNotFoundError(val nodeId: String) : NodeUpdateParamsError()
}

class LinkNotFoundError(val linkId: String) : BusinessError

class NodeNotFoundError(val nodeId: String) : BusinessError

class TransmissionLineNotFoundError : BusinessError

class TransmissionLineDuplicationError(
    val name: String
) : BusinessError

class TransmissionLineContainsSegmentsError(
    val name: String
) : BusinessError

class SubstationNotFoundError : BusinessError

class SubstationDuplicationError(
    val name: String
) : BusinessError

class SubstationContainsEquipmentsError(
    val name: String
) : BusinessError
