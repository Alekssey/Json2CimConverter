package ru.nti.dtps.cimconverter.tocim.extension

import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto

fun RawEquipmentLinkDto.findSourcePortOrNull(
    equipment: RawEquipmentNodeDto
): RawEquipmentNodeDto.PortDto? {
    return equipment.ports.firstOrNull { it.id == sourcePort }
}

fun RawEquipmentLinkDto.findTargetPortOrNull(
    equipment: RawEquipmentNodeDto
): RawEquipmentNodeDto.PortDto? {
    return equipment.ports.firstOrNull { it.id == targetPort }
}
