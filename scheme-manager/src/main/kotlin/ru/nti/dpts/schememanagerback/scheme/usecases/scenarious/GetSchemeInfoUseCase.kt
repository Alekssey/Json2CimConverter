package ru.nti.dpts.schememanagerback.scheme.usecases.scenarious

import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectExtractor
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getSubstationId
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getTransmissionLineIds
import ru.nti.dpts.schememanagerback.scheme.usecases.GetSchemeInfo
import ru.nti.dpts.schememanagerback.scheme.usecases.SchemeInfo
import ru.nti.dtps.equipment.meta.info.extension.isTransmissionLine
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId

private const val NO_ID = "noId"
private const val UNALLOCATED_NAME = "Нераспределенные"

@Component
class GetSchemeInfoUseCase(
//    private val projectExtractor: ProjectExtractor
)  {

//    override fun execute(projectId: String): SchemeInfo {
//        return projectExtractor.extract(projectId).let {
//            val equipmentsInfo = it.scheme.prepareEquipmentsInfo()
//            val substationsInfo = it.scheme.prepareSubstationsInfo()
//            val transmissionLinesInfo = it.scheme.prepareTransmissionLinesInfo()
//
//            SchemeInfo(
//                equipmentsInfo,
//                substationsInfo,
//                transmissionLinesInfo
//            )
//        }
//    }
//
//    private fun SchemeDomain.prepareEquipmentsInfo(): SchemeInfo.EquipmentsInfo {
//        val equipmentsCount = this.nodes.values
//            .filter { node -> node.libEquipmentId != EquipmentLibId.CONNECTIVITY }
//            .size
//
//        return this.nodes.values
//            .map { it.libEquipmentId }
//            .toSet()
//            .filter { it != EquipmentLibId.CONNECTIVITY }
//            .associateWith { equipmentLibId ->
//                this.nodes.values
//                    .filter { it.libEquipmentId == equipmentLibId }
//                    .size
//            }
//            .let { equipmentLibIdToCountMap ->
//                SchemeInfo.EquipmentsInfo(
//                    equipmentsCount,
//                    equipmentLibIdToCountMap
//                )
//            }
//    }

    private fun SchemeDomain.prepareSubstationsInfo(): SchemeInfo.SubstationsInfo {
        return this.substations
            .associate { substation ->
                substation.name to this.nodes.values
                    .filter {
                        !it.libEquipmentId.isTransmissionLine() &&
                            it.libEquipmentId != EquipmentLibId.TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT &&
                            it.libEquipmentId != EquipmentLibId.CONNECTIVITY &&
                            it.getSubstationId() == substation.id
                    }
                    .size
            }.let { substationNameToBelongEquipmentsCountMap ->
                val unallocatedSubstationToBelongEquipmentsCountMap = mapOf(
                    UNALLOCATED_NAME to this.nodes.values
                        .filter {
                            !it.libEquipmentId.isTransmissionLine() &&
                                it.libEquipmentId != EquipmentLibId.TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT &&
                                it.libEquipmentId != EquipmentLibId.CONNECTIVITY &&
                                it.getSubstationId() == NO_ID
                        }
                        .size
                )

                SchemeInfo.SubstationsInfo(
                    this.substations.size,
                    substationNameToBelongEquipmentsCountMap +
                        unallocatedSubstationToBelongEquipmentsCountMap
                )
            }
    }

    private fun SchemeDomain.prepareTransmissionLinesInfo(): SchemeInfo.TransmissionLinesInfo {
        return this.transmissionLines
            .associate { transmissionLine ->
                transmissionLine.name to this.nodes.values
                    .filter {
                        (
                            it.libEquipmentId.isTransmissionLine() ||
                                it.libEquipmentId == EquipmentLibId.TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT
                            ) &&
                            it.getTransmissionLineIds().contains(transmissionLine.id)
                    }
                    .size
            }
            .let { transmissionLineNameToBelongSegmentsCountMap ->
                val unallocatedTransmissionLineToBelongSegmentsCountMap = mapOf(
                    UNALLOCATED_NAME to this.nodes.values
                        .filter {
                            (
                                it.libEquipmentId.isTransmissionLine() ||
                                    it.libEquipmentId == EquipmentLibId.TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT
                                ) &&
                                it.getTransmissionLineIds().contains(NO_ID)
                        }
                        .size
                )

                SchemeInfo.TransmissionLinesInfo(
                    this.transmissionLines.size,
                    transmissionLineNameToBelongSegmentsCountMap +
                        unallocatedTransmissionLineToBelongSegmentsCountMap
                )
            }
    }
}
