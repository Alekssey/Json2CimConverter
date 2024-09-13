package ru.nti.dtps.cimconverter.fromcim.extractor

import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto

data class EquipmentsExtractionResult(
    val equipments: List<RawEquipmentNodeDto>,
    val updatedLinks: List<RawEquipmentLinkDto> = emptyList()
)
