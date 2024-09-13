package ru.nti.dtps.cimconverter.tocim.equipment.auxiliary

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.dto.scheme.isBus
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.utils.graph.DtpsRawSchemeSearch

internal fun findNearestEquipmentAndPort(
    shortCircuit: RawEquipmentNodeDto,
    scheme: RawSchemeDto,
    equipmentIdToResourceMap: Map<String, RdfResource>,
    portIdToTerminalResourceMap: Map<String, RdfResource>
): Pair<RdfResource, RdfResource> {
    var nearestEquipment: RawEquipmentNodeDto? = null
    var nearestPort: RawEquipmentNodeDto.PortDto? = null

    val boundaryPredicate: (sibling: RawEquipmentNodeDto) -> Boolean = {
        nearestEquipment != null && nearestPort != null
    }

    val bfs = DtpsRawSchemeSearch.getBreadthFirstSearch(scheme, boundaryPredicate)

    bfs.searchFromNodeAndSpecialPort(
        shortCircuit,
        shortCircuit.ports.first(),
        doOnSiblingNodeAndPort = { sibling, siblingPort ->
            if (
                sibling.libEquipmentId != EquipmentLibId.SHORT_CIRCUIT &&
                sibling.libEquipmentId != EquipmentLibId.CONNECTIVITY &&
                equipmentIdToResourceMap.containsKey(sibling.id)
            ) {
                nearestEquipment = sibling
                nearestPort = if (sibling.isBus()) {
                    sibling.ports.first { portIdToTerminalResourceMap.containsKey(it.id) }
                } else {
                    siblingPort
                }
            }
        }
    )

    return equipmentIdToResourceMap[nearestEquipment!!.id]!! to portIdToTerminalResourceMap[nearestPort!!.id]!!
}
