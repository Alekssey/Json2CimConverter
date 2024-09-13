package ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.builder

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.GetLinkIdsAssociatedWithConnectivityNodeContainers
import ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.LinkIdAndConnectivityNodeEffect
import ru.nti.dpts.schememanagerback.scheme.service.graphsearch.BreadthFirstSearch
import ru.nti.dpts.schememanagerback.scheme.service.graphsearch.EquipmentNodePredicate
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getNameOrId
import ru.nti.dpts.schememanagerback.service.converter.scl2007B4.TBay
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId

class BaysBuilder {

    private val counter = ObjectsCounter()

    fun build(
        scheme: SchemeDomain,
        substationName: String,
        voltageLevelContainer: VoltageLevelContainer,
        isItPowerTransformer: EquipmentNodePredicate,
        registerLinkAndCnBinding: LinkIdAndConnectivityNodeEffect,
        getLinkIdsAssociatedWithConnectivityNodeContainers: GetLinkIdsAssociatedWithConnectivityNodeContainers
    ): List<BayContainer> {
        return buildForBuses(
            scheme,
            substationName,
            voltageLevelContainer,
            registerLinkAndCnBinding
        ).let { bayContainers ->
            bayContainers + buildForRestScheme(
                scheme,
                substationName,
                voltageLevelContainer,
                isItPowerTransformer,
                bayContainers.flatMap { it.equipments.keys }.toSet(),
                registerLinkAndCnBinding,
                getLinkIdsAssociatedWithConnectivityNodeContainers
            )
        }
    }

    /**
     * Bays contain all elements between bus and power transformer (exclude power transformer) or bus and another bus
     * or bus and transmission line (include transmission line).
     */
    private fun buildForBuses(
        scheme: SchemeDomain,
        substationName: String,
        voltageLevelContainer: VoltageLevelContainer,
        registerLinkAndCnBinding: LinkIdAndConnectivityNodeEffect
    ): List<BayContainer> {
        val buses = voltageLevelContainer.equipments.values.filter { it.libEquipmentId == EquipmentLibId.BUS }
        val bfs = BreadthFirstSearch(scheme) { sibling ->
            sibling.libEquipmentId != EquipmentLibId.BUS || sibling.libEquipmentId != EquipmentLibId.CONNECTIVITY
        }
        val isSiblingAPartOfABusBay: EquipmentNodePredicate = { sibling ->
            voltageLevelContainer.equipments.containsKey(sibling.id) &&
                (sibling.libEquipmentId == EquipmentLibId.BUS || sibling.libEquipmentId == EquipmentLibId.CONNECTIVITY)
        }
        return buses.map { bus ->
            val equipments = mutableMapOf(bus.id to bus)
            bfs.searchFromNode(bus) { sibling, _, _ ->
                if (isSiblingAPartOfABusBay(sibling)) equipments[sibling.id] = sibling
            }
            BayContainer(createBay(), equipments).also {
                it.connectivityNodeContainers = ConnectivityNodesBuilder().createConnectivityNodesForBuses(
                    scheme,
                    substationName,
                    voltageLevelContainer,
                    it,
                    registerLinkAndCnBinding
                )
            }.also { it.bay.connectivityNode.addAll(it.connectivityNodeContainers.map { c -> c.connectivityNode }) }
        }
    }

    private fun buildForRestScheme(
        scheme: SchemeDomain,
        substationName: String,
        voltageLevelContainer: VoltageLevelContainer,
        isItPowerTransformer: EquipmentNodePredicate,
        associatedWithBaysEquipmentIds: Set<String>,
        registerLinkAndCnBinding: LinkIdAndConnectivityNodeEffect,
        getLinkIdsAssociatedWithConnectivityNodeContainers: GetLinkIdsAssociatedWithConnectivityNodeContainers
    ): List<BayContainer> {
        val boundPredicate: EquipmentNodePredicate = { node ->
            associatedWithBaysEquipmentIds.contains(node.id) ||
                !voltageLevelContainer.equipments.containsKey(node.id) ||
                isItPowerTransformer(node)
        }
        val bfs = BreadthFirstSearch(scheme, boundPredicate)
        return voltageLevelContainer.equipments.values.asSequence()
            .filter { !bfs.visitedNodeIds.contains(it.id) && !boundPredicate(it) }
            .map { equipment ->
                val equipments = mutableMapOf(equipment.id to equipment)
                bfs.searchFromNode(equipment) { sibling, _, _ ->
                    if (!boundPredicate(sibling)) equipments[sibling.id] = sibling
                }
                val connectivityNodesBuilder = ConnectivityNodesBuilder()
                BayContainer(createBay(), equipments)
                    .also {
                        it.connectivityNodeContainers =
                            connectivityNodesBuilder.createConnectivityNodesForRestScheme(
                                scheme,
                                substationName,
                                voltageLevelContainer,
                                it,
                                isItPowerTransformer,
                                registerLinkAndCnBinding,
                                getLinkIdsAssociatedWithConnectivityNodeContainers
                            )
                    }
                    .also {
                        it.bay.connectivityNode.addAll(it.connectivityNodeContainers.map { c -> c.connectivityNode })
                    }
            }.toList()
    }

    private fun createBay() = TBay().apply {
        name = "BAY${counter.increaseAndGet()}"
    }
}

class BayContainer(
    val bay: TBay,
    val equipments: Map<String, EquipmentNodeDomain>
) {
    var connectivityNodeContainers: List<ConnectivityNodeContainer> = listOf()

    override fun toString() = equipments.values.joinToString { it.getNameOrId() } +
        (connectivityNodeContainers.joinToString(prefix = " [", postfix = "]") { it.connectivityNode.name })
}
