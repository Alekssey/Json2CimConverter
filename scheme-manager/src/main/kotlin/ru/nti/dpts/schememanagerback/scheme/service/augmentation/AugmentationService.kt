package ru.nti.dpts.schememanagerback.scheme.service.augmentation

import org.springframework.stereotype.Service
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.PortDto
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.service.graphsearch.BreadthFirstSearch
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getVoltageInKilovolts
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getVoltageInKilovoltsByTransformerType
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.selectOps
import ru.nti.dpts.schememanagerback.scheme.service.ops.findNearestVoltageLevelByVoltageInKilovolts
import ru.nti.dtps.equipment.meta.info.extension.isPowerTransformer
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId

/**
 * Service infer additional information from a scheme and add it to the scheme.
 * Additional information:
 *  - voltage level
 */
@Service
class AugmentationService {

    private val sourceLibIds = setOf(
        EquipmentLibId.POWER_SYSTEM_EQUIVALENT,
        EquipmentLibId.SYNCHRONOUS_GENERATOR,
        EquipmentLibId.SOURCE_OF_ELECTROMOTIVE_FORCE_DC
    )

    fun augment(scheme: SchemeDomain) {
        if (scheme.nodes.values.any { it.libEquipmentId.isPowerTransformer() }) {
            assignVoltageLevelIfThereIsPowerTransformerInScheme(scheme)
        } else {
            assignVoltageLevelIfThereIsNoPowerTransformerInScheme(scheme)
        }
        addControlDefaultValuesAndBounds(scheme)
    }

    private fun addControlDefaultValuesAndBounds(scheme: SchemeDomain) {
        scheme.nodes.values.forEach { equipmentNode ->
            equipmentNode.apply {
                controls = selectOps(equipmentNode.libEquipmentId)
                    .createControlsWithDefaultValuesAndBounds(this.fields)
            }
        }
        scheme.dashboardNodes.values.forEach { equipmentNode ->
            equipmentNode.apply {
                controls = selectOps(equipmentNode.libEquipmentId)
                    .createControlsWithDefaultValuesAndBounds(this.fields)
            }
        }
    }

    private fun assignVoltageLevelIfThereIsPowerTransformerInScheme(scheme: SchemeDomain) {
        BreadthFirstSearch(scheme) { node -> node.libEquipmentId.isPowerTransformer() }.also { bfs ->
            scheme.nodes.values
                .filter { it.libEquipmentId.isPowerTransformer() }
                .forEach { powerTransformer ->
                    powerTransformer.ports.forEach { port ->
                        val voltageLevelId = getVoltageLevelIdByPortForPowerTransformer(powerTransformer, port)
                        bfs.searchFromNodeAndSpecialPortAndDoOnSiblings(powerTransformer, port) { sibling, link, _ ->
                            if (sibling.libEquipmentId.isPowerTransformer()) {
                                val previousVoltageLevelLib = sibling.voltageLevelId?.let {
                                    EquipmentMetaInfoManager.getVoltageLevelById(it)
                                }
                                val targetVoltageLevelLib = EquipmentMetaInfoManager.getVoltageLevelById(
                                    voltageLevelId
                                )
                                sibling.voltageLevelId = if (previousVoltageLevelLib == null) {
                                    targetVoltageLevelLib.id
                                } else {
                                    if (targetVoltageLevelLib.voltageInKilovolts
                                        >= previousVoltageLevelLib.voltageInKilovolts
                                    ) {
                                        targetVoltageLevelLib.id
                                    } else {
                                        previousVoltageLevelLib.id
                                    }
                                }
                            } else {
                                sibling.voltageLevelId = voltageLevelId
                            }
                            link.voltageLevelId = voltageLevelId
                        }
                        if (port.libId == PortLibId.FIRST) {
                            powerTransformer
                                .apply { this.voltageLevelId = voltageLevelId }
                        }
                    }
                }
        }
    }

    private fun assignVoltageLevelIfThereIsNoPowerTransformerInScheme(scheme: SchemeDomain) {
        BreadthFirstSearch(scheme).also { bfs ->
            scheme.nodes.values
                .filter { sourceLibIds.contains(it.libEquipmentId) }
                .forEach { sourceEquipment ->
                    if (!bfs.visitedNodeIds.contains(sourceEquipment.id)) {
                        val voltageLevelId = getVoltageLevelIdSourceEquipment(sourceEquipment)
                        bfs.searchFromNodeAndSpecialPortAndDoOnSiblings(
                            sourceEquipment,
                            sourceEquipment.ports.first()
                        ) { sibling, link, _ ->
                            sibling.voltageLevelId = voltageLevelId
                            link.voltageLevelId = voltageLevelId
                        }
                        sourceEquipment.apply { this.voltageLevelId = voltageLevelId }
                    }
                }
        }
    }

    private fun getVoltageLevelIdByPortForPowerTransformer(
        powerTransformer: EquipmentNodeDomain,
        portDto: PortDto
    ): VoltageLevelLibId {
        return findNearestVoltageLevelByVoltageInKilovolts(
            powerTransformer.getVoltageInKilovoltsByTransformerType(portDto)
        ).id
    }

    private fun getVoltageLevelIdSourceEquipment(
        source: EquipmentNodeDomain
    ): VoltageLevelLibId {
        return findNearestVoltageLevelByVoltageInKilovolts(
            source.getVoltageInKilovolts()
        ).id
    }
}
