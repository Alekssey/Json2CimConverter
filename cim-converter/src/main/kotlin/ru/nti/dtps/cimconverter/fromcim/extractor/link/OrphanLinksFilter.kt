package ru.nti.dtps.cimconverter.fromcim.extractor.link

import ru.nti.dtps.cimconverter.tocim.extension.findSourcePortOrNull
import ru.nti.dtps.cimconverter.tocim.extension.findTargetPortOrNull
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto

object OrphanLinksFilter {

    fun filter(
        allLinks: List<RawEquipmentLinkDto>,
        equipments: Map<String, RawEquipmentNodeDto>
    ): List<RawEquipmentLinkDto> = allLinks.filter { link ->
        val source = equipments[link.source]
        val target = equipments[link.target]

        val sourcePort = source?.let { link.findSourcePortOrNull(it) }
        val targetPort = target?.let { link.findTargetPortOrNull(it) }

        source != null && target != null && sourcePort != null && targetPort != null
    }
}
