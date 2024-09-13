package ru.nti.dpts.schememanagerback.scheme.usecases

import ru.nti.dtps.proto.lib.equipment.EquipmentLibId

interface GetSchemeInfo {
    fun execute(projectId: String): SchemeInfo
}

data class SchemeInfo(
    val equipmentsInfo: EquipmentsInfo,
    val substationsInfo: SubstationsInfo,
    val transmissionLinesInfo: TransmissionLinesInfo
) {
    data class EquipmentsInfo(
        val equipmentsCount: Int,
        val equipmentLibIdToCountMap: Map<EquipmentLibId, Int>
    )

    data class SubstationsInfo(
        val substationsCount: Int,
        val substationNameToBelongEquipmentsCountMap: Map<String, Int>
    )

    data class TransmissionLinesInfo(
        val transmissionLinesCount: Int,
        val transmissionLineNameToBelongSegmentsCountMap: Map<String, Int>
    )
}
