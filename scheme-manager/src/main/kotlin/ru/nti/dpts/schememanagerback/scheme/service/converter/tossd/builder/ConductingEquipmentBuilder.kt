package ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.builder

import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getNameOrId
import ru.nti.dpts.schememanagerback.service.converter.scl2007B4.TConductingEquipment
import ru.nti.dpts.schememanagerback.service.converter.scl2007B4.TPredefinedCommonConductingEquipmentEnum
import ru.nti.dtps.equipment.meta.info.dataclass.common.Language
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId.*

class ConductingEquipmentBuilder {

    fun build(
        bayContainer: BayContainer,
        linkIdToConnectivityNodeContainer: Map<String, ConnectivityNodeContainer>
    ): List<TConductingEquipment> {
        return bayContainer.equipments.values
            .filter { it.libEquipmentId != BUS && it.libEquipmentId != CONNECTIVITY }
            .flatMap { createConductingEquipment(it, linkIdToConnectivityNodeContainer) }
    }

    private fun createConductingEquipment(
        equipment: EquipmentNodeDomain,
        linkIdToConnectivityNodeContainer: Map<String, ConnectivityNodeContainer>
    ): List<TConductingEquipment> {
        return listOf(
            createConductingEquipment(
                equipment.getNameOrId(),
                getConductingEquipmentType(equipment.libEquipmentId),
                EquipmentMetaInfoManager.getEquipmentLibById(equipment.libEquipmentId).name[Language.RU]!!,
                equipment.ports.mapNotNull { port -> linkIdToConnectivityNodeContainer[port.links[0]] }
            )
        )
    }

    private fun createConductingEquipment(
        conductingEquipmentName: String,
        conductingEquipmentType: String,
        conductingEquipmentDesc: String,
        cnContainers: List<ConnectivityNodeContainer>
    ) = TConductingEquipment().apply {
        name = conductingEquipmentName
        type = conductingEquipmentType
        desc = conductingEquipmentDesc
        terminal.addAll(cnContainers.map { buildTerminal(it) })
    }

    private fun getConductingEquipmentType(equipmentLibId: EquipmentLibId): String {
        return when (equipmentLibId) {
            CIRCUIT_BREAKER -> TPredefinedCommonConductingEquipmentEnum.CBR.name
            // short circuit element doesn't map directly in SCL, so I decide to map it to surge arrester
            SHORT_CIRCUIT -> TPredefinedCommonConductingEquipmentEnum.SAR.name
            // source and load elements like power system equivalent doesn't map directly in SCL, so I decide to map it
            // to transmission line
            TRANSMISSION_LINE_SEGMENT,
            LOAD,
            ASYNCHRONOUS_MOTOR,
            POWER_SYSTEM_EQUIVALENT,
            GROUNDING,
            ELECTRICITY_STORAGE_SYSTEM,
            TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT -> TPredefinedCommonConductingEquipmentEnum.IFL.name
            RESISTANCE -> TPredefinedCommonConductingEquipmentEnum.RES.name
            INDUCTANCE -> TPredefinedCommonConductingEquipmentEnum.REA.name
            SYNCHRONOUS_GENERATOR -> TPredefinedCommonConductingEquipmentEnum.GEN.name
            CURRENT_TRANSFORMER -> TPredefinedCommonConductingEquipmentEnum.CTR.name
            VOLTAGE_TRANSFORMER -> TPredefinedCommonConductingEquipmentEnum.VTR.name
            REACTIVE_POWER_COMPENSATION_SYSTEM,
            CAPACITANCE -> TPredefinedCommonConductingEquipmentEnum.CAP.name
            DISCONNECTOR -> TPredefinedCommonConductingEquipmentEnum.DIS.name
            GROUND_DISCONNECTOR -> TPredefinedCommonConductingEquipmentEnum.EFN.name
            CURRENT_SOURCE_DC,
            SOURCE_OF_ELECTROMOTIVE_FORCE_DC -> TPredefinedCommonConductingEquipmentEnum.PSH.name
            else -> throw RuntimeException("Unexpected equipment lib type: $equipmentLibId")
        }
    }
}
