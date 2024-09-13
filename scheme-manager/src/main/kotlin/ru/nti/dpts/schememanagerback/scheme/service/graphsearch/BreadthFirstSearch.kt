package ru.nti.dpts.schememanagerback.scheme.service.graphsearch

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentLinkDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.PortDto
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getRelatedPort
import ru.nti.dpts.schememanagerback.scheme.service.ops.getLinksFromScheme
import ru.nti.dpts.schememanagerback.scheme.service.ops.getSiblingFromSchemeOf
import java.util.*

typealias DoOnSiblings = (node: EquipmentNodeDomain, link: EquipmentLinkDomain, port: PortDto) -> Unit
typealias EquipmentNodePredicate = (node: EquipmentNodeDomain) -> Boolean

private val doNothing: DoOnSiblings = { _, _, _ -> Unit }

class BreadthFirstSearch(
    private val scheme: SchemeDomain,
    private val boundaryPredicate: EquipmentNodePredicate = { false }
) {

    val visitedNodeIds = mutableSetOf<String>()

    fun searchFromNode(from: EquipmentNodeDomain, doOnSiblings: DoOnSiblings = doNothing) {
        val queue: Queue<EquipmentNodeDomain> = LinkedList(listOf(from))
        walkThroughScheme(queue, doOnSiblings)
    }

    fun searchFromNodeAndSpecialPortAndDoOnSiblings(
        fromNode: EquipmentNodeDomain,
        fromPort: PortDto,
        doOnSiblings: DoOnSiblings
    ) {
        visitedNodeIds.add(fromNode.id)
        fromNode.ports.filter { it.id == fromPort.id }.forEach { port ->
            val siblings = getSiblingsAndDoOnItIfNotVisited(fromNode, port, doOnSiblings)
            siblings.forEach { sibling ->
                if (!visitedNodeIds.contains(sibling.id) && !boundaryPredicate(sibling)) {
                    val queue: Queue<EquipmentNodeDomain> = LinkedList(listOf(sibling))
                    walkThroughScheme(queue, doOnSiblings)
                }
            }
        }
    }

    private tailrec fun walkThroughScheme(
        nodes: Queue<EquipmentNodeDomain>,
        doOnSiblings: DoOnSiblings
    ) {
        if (nodes.isEmpty()) return
        nodes.poll().let { node ->
            visitedNodeIds.add(node.id)
            node.ports.forEach { port ->
                val siblings = getSiblingsAndDoOnItIfNotVisited(node, port, doOnSiblings)
                siblings.forEach { sibling ->
                    if (!visitedNodeIds.contains(sibling.id) && !boundaryPredicate(sibling)) {
                        nodes.add(sibling)
                    }
                }
            }
        }
        walkThroughScheme(nodes, doOnSiblings)
    }

    private fun getSiblingsAndDoOnItIfNotVisited(
        fromNode: EquipmentNodeDomain,
        fromPort: PortDto,
        doOnSiblings: DoOnSiblings
    ): List<EquipmentNodeDomain> {
        return fromPort
            .getLinksFromScheme(scheme)
            .asSequence()
            .map { link ->
                link.getSiblingFromSchemeOf(scheme, fromNode)
                    .also {
                        if (!visitedNodeIds.contains(it.id)) {
                            doOnSiblings(it, link, it.getRelatedPort(link))
                        }
                    }
            }
            .toList()
    }
}
