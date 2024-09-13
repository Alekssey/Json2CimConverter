package ru.nti.dtps.cimconverter.fromcim.extractor.equipment

import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObject
import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.impl.*
import ru.nti.dtps.cimconverter.fromcim.extractor.link.PortInfo
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.basevoltage.BaseVoltage
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.voltagelevel.VoltageLevel
import ru.nti.dtps.cimconverter.fromcim.rdf.repository.RdfRepository
import ru.nti.dtps.dto.scheme.RawEquipmentLinkDto
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.equipment.meta.info.dataclass.equipment.field.FieldValueType
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import java.util.*
import kotlin.reflect.KClass

private val extractors: List<SealedEquipmentExtractor> =
    SealedEquipmentExtractor::class
        .sealedSubclasses
        .mapNotNull(KClass<out SealedEquipmentExtractor>::objectInstance)

object EquipmentsExtractor {

    fun extract(
        repository: RdfRepository,
        substations: Map<String, RawSchemeDto.SubstationDto>,
        lines: Map<String, RawSchemeDto.TransmissionLineDto>,
        baseVoltages: Map<String, BaseVoltage>,
        voltageLevels: Map<String, VoltageLevel>,
        equipmentIdToPortsMap: Map<String, List<PortInfo>>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>,
        links: List<RawEquipmentLinkDto>,
        getEquipmentFrequencyOrDefault: (equipmentId: String) -> Double
    ): Pair<List<RawEquipmentNodeDto>, List<RawEquipmentLinkDto>> {
        val equipments: MutableList<RawEquipmentNodeDto> = mutableListOf()
        val updatedLinks: MutableList<RawEquipmentLinkDto> = mutableListOf()

        extractors.forEach { extractor ->
            val extractionResult = extractor.extract(
                repository,
                substations,
                lines,
                baseVoltages,
                voltageLevels,
                equipmentIdToPortsMap,
                objectIdToDiagramObjectMap,
                links,
                getEquipmentFrequencyOrDefault
            )

            equipments += extractionResult.equipments.map(::limitFieldValues)
            updatedLinks += extractionResult.updatedLinks
        }

        val allLinks = updatedLinks
            .associateBy(RawEquipmentLinkDto::id)
            .let { linkIdToUpdatedLinkMap -> links.map { link -> linkIdToUpdatedLinkMap[link.id] ?: link } }

        return equipments to allLinks
    }

    private fun limitFieldValues(
        equipment: RawEquipmentNodeDto
    ): RawEquipmentNodeDto = equipment.copy(
        fields = equipment.fields.mapValues { (fieldLibId, value) ->
            val fieldLib = EquipmentMetaInfoManager.getFieldByEquipmentLibIdAndFieldLibId(
                equipment.libEquipmentId,
                fieldLibId
            )

            val fieldLibType = EquipmentMetaInfoManager.getFieldTypeById(fieldLib.typeId)

            if (fieldLibType.valueType == FieldValueType.NUMBER) {
                val min = fieldLib.min ?: fieldLibType.min!!
                val max = fieldLib.max ?: fieldLibType.max!!

                val doubleValue = value!!.toDouble()

                when {
                    doubleValue < min -> min
                    doubleValue > max -> max
                    else -> doubleValue
                }.toString()
            } else {
                value
            }
        }
    )
}
