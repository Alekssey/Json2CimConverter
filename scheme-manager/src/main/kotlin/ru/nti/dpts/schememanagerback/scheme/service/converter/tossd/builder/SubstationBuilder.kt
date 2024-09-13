package ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.builder

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.Substation
import ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.NO_SUBSTATION_ID
import ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.isItPowerTransformer
import ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.isItTransmissionLine
import ru.nti.dpts.schememanagerback.scheme.service.graphsearch.BreadthFirstSearch
import ru.nti.dpts.schememanagerback.scheme.service.graphsearch.EquipmentNodePredicate
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getSubstationId
import ru.nti.dpts.schememanagerback.service.converter.scl2007B4.TSubstation

class SubstationBuilder {

    fun build(
        scheme: SchemeDomain,
        substation: Substation,
        substationEquipment: List<EquipmentNodeDomain>
    ): TSubstation {
        val definedSubstationEquipments = defineAllSubstationEquipments(
            scheme,
            substation,
            substationEquipment
        )
        val powerTransformerEquipments = definedSubstationEquipments.filter(isItPowerTransformer)
        val linkIdToConnectivityNodeContainer = mutableMapOf<String, ConnectivityNodeContainer>()
        val voltageLevelContainers = VoltageLevelBuilder().build(
            scheme,
            substation,
            definedSubstationEquipments,
            { linkId, cnContainer -> linkIdToConnectivityNodeContainer[linkId] = cnContainer },
            { linkIdToConnectivityNodeContainer.keys }
        )

        voltageLevelContainers
            .flatMap { vlContainer -> vlContainer.bayContainers }
            .forEach { bayContainer ->
                bayContainer.bay.conductingEquipment.addAll(
                    ConductingEquipmentBuilder().build(
                        bayContainer,
                        linkIdToConnectivityNodeContainer
                    )
                )
            }

        return TSubstation().apply {
            name = substation.name.replace("""[<>:"/\\|?*]""".toRegex(), "_")
            powerTransformer.addAll(
                PowerTransformerBuilder().createPowerTransformers(
                    powerTransformerEquipments,
                    linkIdToConnectivityNodeContainer
                )
            )
            voltageLevel.addAll(
                voltageLevelContainers.map { it.voltageLevel }
            )
        }
    }

    private fun defineAllSubstationEquipments(
        scheme: SchemeDomain,
        substation: Substation,
        markedEquipments: List<EquipmentNodeDomain>
    ): Collection<EquipmentNodeDomain> {
        val boundPredicate: EquipmentNodePredicate = { node ->
            isItTransmissionLine(node) ||
                (node.getSubstationId() != NO_SUBSTATION_ID && node.getSubstationId() != substation.id)
        }
        val bfs = BreadthFirstSearch(scheme, boundPredicate)

        return mutableMapOf<String, EquipmentNodeDomain>().also { substationEquipments ->
            markedEquipments.asSequence()
                .filter { !bfs.visitedNodeIds.contains(it.id) }
                .forEach { equipment ->
                    substationEquipments[equipment.id] = equipment
                    bfs.searchFromNode(equipment) { sibling, _, _ ->
                        if (!boundPredicate(sibling)) {
                            substationEquipments[sibling.id] = sibling
                        }
                    }
                }
        }.values
    }
}
