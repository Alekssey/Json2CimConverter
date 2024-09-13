package ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.builder

import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.GetLinkIdsAssociatedWithConnectivityNodeContainers
import ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.LinkIdAndConnectivityNodeEffect
import ru.nti.dpts.schememanagerback.scheme.service.graphsearch.BreadthFirstSearch
import ru.nti.dpts.schememanagerback.scheme.service.graphsearch.EquipmentNodePredicate
import ru.nti.dpts.schememanagerback.scheme.service.ops.getLinksFromScheme
import ru.nti.dpts.schememanagerback.service.converter.scl2007B4.TConnectivityNode
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId

class ConnectivityNodesBuilder {

    private val counter = ObjectsCounter()

    fun createConnectivityNodesForBuses(
        scheme: SchemeDomain,
        substationName: String,
        voltageLevelContainer: VoltageLevelContainer,
        bayContainer: BayContainer,
        registerLinkAndCnBinding: LinkIdAndConnectivityNodeEffect
    ): List<ConnectivityNodeContainer> {
        val linkIds = mutableSetOf<String>()
        bayContainer.equipments.values.map { equipment ->
            equipment.ports.forEach { p ->
                linkIds.addAll(p.getLinksFromScheme(scheme).map { it.id })
            }
        }

        return ConnectivityNodeContainer(
            createConnectivityNode(
                substationName,
                voltageLevelContainer.voltageLevel.name,
                bayContainer.bay.name
            )
        ).also { linkIds.forEach { id -> registerLinkAndCnBinding(id, it) } }.let(::listOf)
    }

    fun createConnectivityNodesForRestScheme(
        scheme: SchemeDomain,
        substationName: String,
        voltageLevelContainer: VoltageLevelContainer,
        bayContainer: BayContainer,
        isItPowerTransformer: EquipmentNodePredicate,
        registerLinkAndCnBinding: LinkIdAndConnectivityNodeEffect,
        getLinkIdsAssociatedWithConnectivityNodeContainers: GetLinkIdsAssociatedWithConnectivityNodeContainers
    ): List<ConnectivityNodeContainer> {
        val boundPredicate: EquipmentNodePredicate = { node ->
            !bayContainer.equipments.containsKey(node.id) || node.libEquipmentId != EquipmentLibId.CONNECTIVITY
        }
        val bfs = BreadthFirstSearch(scheme, boundPredicate)
        val isSiblingLinkRequiredNewConnectivityNode: EquipmentNodePredicate = { sibling ->
            (bayContainer.equipments.containsKey(sibling.id) || isItPowerTransformer(sibling)) &&
                !bfs.visitedNodeIds.contains(sibling.id)
        }
        return bayContainer.equipments.values.asSequence()
            .filter { !bfs.visitedNodeIds.contains(it.id) }
            .flatMap { equipment ->
                val connectivityNodeContainers = mutableListOf<ConnectivityNodeContainer>()
                equipment.ports.forEach { port ->
                    val linkIds = mutableSetOf<String>()
                    val linksAssociatedWithCNContainers = getLinkIdsAssociatedWithConnectivityNodeContainers()
                    bfs.searchFromNodeAndSpecialPortAndDoOnSiblings(equipment, port) { sibling, link, _ ->
                        if (isSiblingLinkRequiredNewConnectivityNode(sibling) &&
                            !linksAssociatedWithCNContainers.contains(link.id)
                        ) {
                            linkIds.add(link.id)
                        }
                    }
                    if (linkIds.size != 0) {
                        connectivityNodeContainers.add(
                            ConnectivityNodeContainer(
                                createConnectivityNode(
                                    substationName,
                                    voltageLevelContainer.voltageLevel.name,
                                    bayContainer.bay.name
                                )
                            ).also { linkIds.forEach { id -> registerLinkAndCnBinding(id, it) } }
                        )
                    }
                }
                connectivityNodeContainers
            }.toList()
    }

    private fun createConnectivityNode(
        substationName: String,
        voltageLevelName: String,
        bayName: String
    ) = "BCN${counter.increaseAndGet()}".let { connectivityNodeName ->
        TConnectivityNode().apply {
            name = connectivityNodeName
            pathName = "$substationName/$voltageLevelName/$bayName/$connectivityNodeName"
        }
    }
}

class ConnectivityNodeContainer(
    val connectivityNode: TConnectivityNode
) {
    override fun toString() =
        (connectivityNode.name ?: super.toString()) + " [" + (connectivityNode.pathName ?: "") + "]"
}
