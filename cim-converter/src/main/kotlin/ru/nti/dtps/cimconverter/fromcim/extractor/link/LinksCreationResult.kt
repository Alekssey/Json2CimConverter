package ru.nti.dtps.cimconverter.fromcim.extractor.link

import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto

data class LinksCreationResult(
    val links: List<RawEquipmentLinkDto>,
    val ports: List<PortInfo>,
    val connectivities: List<RawEquipmentNodeDto> = emptyList()
)
