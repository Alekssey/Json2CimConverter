package ru.nti.dtps.cimconverter.fromcim.extractor.equipment

import org.eclipse.rdf4j.query.BindingSet
import ru.nti.dtps.cimconverter.ext.hasFieldWithId
import ru.nti.dtps.cimconverter.fromcim.extensions.getDefaultEquipmentFields
import ru.nti.dtps.cimconverter.fromcim.extensions.isBus
import ru.nti.dtps.cimconverter.fromcim.extensions.isTransmissionLineSegment
import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObject
import ru.nti.dtps.cimconverter.fromcim.extractor.link.PortInfo
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.basevoltage.BaseVoltage
import ru.nti.dtps.cimconverter.fromcim.extractor.voltage.voltagelevel.VoltageLevel
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIdentifiedObjectId
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractIdentifiedObjectNameOrNull
import ru.nti.dtps.cimconverter.fromcim.sparql.extension.extractObjectReferenceOrNull
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.RawSchemeDto
import ru.nti.dtps.dto.scheme.XyDto
import ru.nti.dtps.equipment.meta.info.dataclass.common.Language
import ru.nti.dtps.equipment.meta.info.dataclass.equipment.EquipmentLib
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId

const val MIN_BUS_WIDTH = 390.0
private const val ADDITIONAL_BUS_EMPTY_SPACE = 150.0

object RawEquipmentCreator {

    fun equipmentWithBaseData(
        bindingSet: BindingSet,
        substations: Map<String, RawSchemeDto.SubstationDto>,
        lines: Map<String, RawSchemeDto.TransmissionLineDto>,
        baseVoltages: Map<String, BaseVoltage>,
        voltageLevels: Map<String, VoltageLevel>,
        number: Int,
        equipmentLibId: EquipmentLibId,
        equipmentIdToPortsMap: Map<String, List<PortInfo>>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>
    ): RawEquipmentNodeDto {
        val equipmentId = bindingSet.extractIdentifiedObjectId()
        val equipmentLib = EquipmentMetaInfoManager.getEquipmentLibById(equipmentLibId)
        val containerId = bindingSet.extractObjectReferenceOrNull(CimClasses.Equipment.EquipmentContainer)

        val diagramObject = objectIdToDiagramObjectMap[equipmentId]
        val coords = getCoords(diagramObject, equipmentLib)

        val equipmentName = bindingSet
            .extractIdentifiedObjectNameOrNull()
            ?: "${equipmentLib.name[Language.RU]} $number"

        val fields: Map<FieldLibId, String?> = run {
            val fields = equipmentLib
                .getDefaultEquipmentFields()
                .toMutableMap()
                .apply { put(FieldLibId.NAME, equipmentName) }

            if (equipmentLib.hasFieldWithId(FieldLibId.SUBSTATION)) {
                fields[FieldLibId.SUBSTATION] = getSubstationId(
                    equipmentLib,
                    substations,
                    voltageLevels,
                    containerId
                )
            }

            if (equipmentLib.hasFieldWithId(FieldLibId.TRANSMISSION_LINE)) {
                fields[FieldLibId.TRANSMISSION_LINE] = getTransmissionLineId(
                    equipmentLib,
                    lines,
                    containerId
                )
            }

            fields
        }

        return RawEquipmentNodeDto(
            id = equipmentId,
            libEquipmentId = equipmentLibId,
            fields = fields,
            voltageLevelId = bindingSet
                .extractObjectReferenceOrNull(CimClasses.ConductingEquipment.BaseVoltage)
                ?.let(baseVoltages::get)?.voltageLevelLib?.id,
            ports = PortsCreator.createPorts(
                equipmentId,
                equipmentLib,
                equipmentIdToPortsMap[equipmentId] ?: listOf(),
                coords
            ),
            coords = coords,
            dimensions = getDimensions(equipmentId, equipmentLib, objectIdToDiagramObjectMap),
            hour = diagramObject?.hour ?: 0
        )
    }

    private fun getTransmissionLineId(
        equipmentLib: EquipmentLib,
        lines: Map<String, RawSchemeDto.TransmissionLineDto>,
        containerId: String?
    ): String? = if (equipmentLib.isTransmissionLineSegment()) {
        lines[containerId]?.id ?: "noId"
    } else {
        null
    }

    private fun getSubstationId(
        equipmentLib: EquipmentLib,
        substations: Map<String, RawSchemeDto.SubstationDto>,
        voltageLevels: Map<String, VoltageLevel>,
        containerId: String?
    ): String? = if (!equipmentLib.isTransmissionLineSegment()) {
        when (containerId) {
            in substations -> substations[containerId]!!.id
            in voltageLevels -> voltageLevels[containerId]!!.substation?.id
            else -> null
        } ?: "noId"
    } else {
        null
    }

    private fun getDimensions(
        equipmentId: String,
        equipmentLib: EquipmentLib,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>
    ): RawEquipmentNodeDto.SizeDto {
        return if (equipmentLib.isBus()) {
            val diagramObject = objectIdToDiagramObjectMap[equipmentId]

            val size: RawEquipmentNodeDto.SizeDto = if (diagramObject != null && diagramObject.points.size > 1) {
                val (minX, maxX) = diagramObject.points.map { it.coords.x }.let {
                    it.min() to it.max()
                }

                val (minY, maxY) = diagramObject.points.map { it.coords.y }.let {
                    it.min() to it.max()
                }

                val width = when (diagramObject.hour) {
                    0, 2 -> maxX - minX + ADDITIONAL_BUS_EMPTY_SPACE
                    1, 3 -> maxY - minY + ADDITIONAL_BUS_EMPTY_SPACE
                    else -> throw RuntimeException("Unexpected hour orientation: ${diagramObject.hour}")
                }.let { width -> if (width < MIN_BUS_WIDTH) MIN_BUS_WIDTH else width }

                RawEquipmentNodeDto.SizeDto(0.0, width)
            } else {
                RawEquipmentNodeDto.SizeDto(0.0, MIN_BUS_WIDTH)
            }

            size
        } else {
            val size = RawEquipmentNodeDto.SizeDto(
                equipmentLib.height[Language.RU]!!,
                equipmentLib.width[Language.RU]!!
            )
            RawEquipmentNodeDto.SizeDto(size.height, size.width)
        }
    }

    private fun getCoords(
        diagramObject: DiagramObject?,
        equipmentLib: EquipmentLib
    ): XyDto {
        val coords: XyDto? = if (diagramObject != null) {
            if (equipmentLib.isBus()) {
                val reduce: (acc: XyDto, elem: XyDto) -> XyDto = when (diagramObject.hour) {
                    0, 2 -> { acc, elem -> if (acc.x <= elem.x) acc else elem }
                    1, 3 -> { acc, elem -> if (acc.y <= elem.y) acc else elem }
                    else -> throw RuntimeException("Unexpected hour orientation: ${diagramObject.hour}")
                }
                val coords = diagramObject.points.map { it.coords }.reduceOrNull(reduce)?.let {
                    XyDto(it.x, it.y)
                }
                coords
            } else {
                diagramObject.points.firstOrNull()?.coords
            }
        } else {
            null
        }

        return coords ?: XyDto(0.0, 0.0)
    }
}
