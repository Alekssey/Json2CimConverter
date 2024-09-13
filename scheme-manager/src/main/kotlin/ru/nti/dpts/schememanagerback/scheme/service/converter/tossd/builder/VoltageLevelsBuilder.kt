package ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.builder

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.Substation
import ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.*
import ru.nti.dpts.schememanagerback.scheme.service.graphsearch.BreadthFirstSearch
import ru.nti.dpts.schememanagerback.scheme.service.graphsearch.EquipmentNodePredicate
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getSubstationId
import ru.nti.dpts.schememanagerback.service.converter.scl2007B4.TVoltage
import ru.nti.dpts.schememanagerback.service.converter.scl2007B4.TVoltageLevel
import ru.nti.dtps.equipment.meta.info.dataclass.voltagelevel.VoltageLevelLib
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId.*

class VoltageLevelBuilder {

    private val counter = ObjectsCounter()

    fun build(
        scheme: SchemeDomain,
        substation: Substation,
        equipments: Collection<EquipmentNodeDomain>,
        registerLinkIdAndCnBinding: LinkIdAndConnectivityNodeEffect,
        getLinkIdsAssociatedWithConnectivityNodeContainers: GetLinkIdsAssociatedWithConnectivityNodeContainers
    ): List<VoltageLevelContainer> {
        val boundPredicate: EquipmentNodePredicate = { node ->
            isItTransmissionLine(node) ||
                (node.getSubstationId() != NO_SUBSTATION_ID && node.getSubstationId() != substation.id) ||
                isItPowerTransformer(node)
        }
        val bfs = BreadthFirstSearch(scheme, boundPredicate)

        return equipments.asSequence()
            .filter { !isItPowerTransformer(it) }
            .filter { !bfs.visitedNodeIds.contains(it.id) }
            .map { equipment ->
                val voltageLevel = createVoltageLevel(EquipmentMetaInfoManager.getVoltageLevelById(equipment.voltageLevelId))
                val voltageLevelEquipments = mutableMapOf(equipment.id to equipment)
                bfs.searchFromNode(equipment) { sibling, _, _ ->
                    if (!isItPowerTransformer(sibling)) {
                        voltageLevelEquipments[sibling.id] = sibling
                    }
                }

                VoltageLevelContainer(
                    voltageLevel,
                    voltageLevelEquipments
                ).also {
                    it.bayContainers = BaysBuilder().build(
                        scheme,
                        substation.name,
                        it,
                        isItPowerTransformer,
                        registerLinkIdAndCnBinding,
                        getLinkIdsAssociatedWithConnectivityNodeContainers
                    )
                }.also {
                    it.voltageLevel.bay.addAll(it.bayContainers.map { bayContainer -> bayContainer.bay })
                }
            }.toList()
    }

    private fun createVoltageLevel(voltageLevelLib: VoltageLevelLib) = TVoltageLevel()
        .apply {
            name = createVoltageLevelName(voltageLevelLib)
            voltage = createVoltage(voltageLevelLib)
        }

    private fun createVoltageLevelName(voltageLevelLib: VoltageLevelLib) = getVoltageLevelLetter(voltageLevelLib)
        .let { letter -> "$letter${counter.increaseAndGet(letter)}" }

    private fun createVoltage(voltageLevelLib: VoltageLevelLib) = TVoltage()
        .apply {
            multiplier = "k"
            unit = "V"
            value = voltageLevelLib.voltageInKilovolts.toInt().toBigDecimal()
        }

    private fun getVoltageLevelLetter(voltageLevelLib: VoltageLevelLib): String {
        return when (voltageLevelLib.id) {
            VOLTS_12,
            VOLTS_24,
            VOLTS_36,
            VOLTS_42,
            VOLTS_127,
            VOLTS_110,
            VOLTS_220,
            VOLTS_380,
            KILOVOLTS_3 -> "R"

            KILOVOLTS_6 -> "P"
            KILOVOLTS_10 -> "K"
            KILOVOLTS_20,
            KILOVOLTS_35 -> "H"

            KILOVOLTS_110,
            KILOVOLTS_150 -> "G"

            KILOVOLTS_220 -> "E"
            KILOVOLTS_330 -> "O"
            KILOVOLTS_400 -> "O"
            KILOVOLTS_500 -> "C"
            KILOVOLTS_750 -> "B"
            KILOVOLTS_1150 -> "A"

            UNRECOGNIZED -> throw IllegalArgumentException("No such type of voltage level ${voltageLevelLib.id.name}")
        }
    }
}

class VoltageLevelContainer(
    val voltageLevel: TVoltageLevel,
    val equipments: Map<String, EquipmentNodeDomain>
) {
    var bayContainers: List<BayContainer> = listOf()
}
