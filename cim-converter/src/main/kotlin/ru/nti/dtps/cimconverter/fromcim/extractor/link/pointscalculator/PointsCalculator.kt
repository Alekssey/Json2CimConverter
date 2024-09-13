package ru.nti.dtps.cimconverter.fromcim.extractor.link.pointscalculator

import ru.nti.dtps.cimconverter.fromcim.extractor.equipment.MIN_BUS_WIDTH
import ru.nti.dtps.cimconverter.rotate
import ru.nti.dtps.dto.scheme.*
import ru.nti.dtps.equipment.meta.info.dataclass.common.Language
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import java.util.*

private val language = Language.RU

object PointsCalculator {

    fun calculateLinkPointsAndReturnLinks(
        allLinks: List<RawEquipmentLinkDto>,
        equipments: Map<String, RawEquipmentNodeDto>
    ): List<RawEquipmentLinkDto> = allLinks.map { link ->
        val source = equipments[link.source]
        val target = equipments[link.target]

        if (source != null && target != null) {
            val sourcePort = source.ports.first { it.id == link.sourcePort }.let {
                it.copy(coords = calculatePortCoordinates(source, it))
            }

            val targetPort = target.ports.first { it.id == link.targetPort }.let {
                it.copy(coords = calculatePortCoordinates(target, it))
            }

            link.copy(
                points = listOf(
                    RawEquipmentLinkDto.PointDto(
                        id = UUID.randomUUID().toString(),
                        coords = sourcePort.coords
                    ),
                    RawEquipmentLinkDto.PointDto(
                        id = UUID.randomUUID().toString(),
                        coords = targetPort.coords
                    )
                )
            )
        } else {
            link
        }
    }

    private fun calculatePortCoordinates(
        equipment: RawEquipmentNodeDto,
        port: RawEquipmentNodeDto.PortDto
    ): XyDto {
        return when {
            equipment.isBus() -> calculatePortCoordinatesForBus(equipment, port)
            equipment.isConnectivity() -> calculatePortCoordinatesForConnectivity(equipment)
            else -> calculatePortCoordinatesForLibraryEquipment(equipment, port)
        }
    }

    private fun calculatePortCoordinatesForBus(
        equipment: RawEquipmentNodeDto,
        port: RawEquipmentNodeDto.PortDto
    ): XyDto {
        val busWidth = if (equipment.dimensions.width == 0.0) {
            MIN_BUS_WIDTH
        } else {
            equipment.dimensions.width
        }

        val (x, y) = rotate(
            equipment.hour,
            equipment.coords.x + busWidth / 2,
            equipment.coords.y,
            equipment.coords.x + port.coords.x,
            equipment.coords.y + port.coords.y
        )
        return XyDto(x, y)
    }

    private fun calculatePortCoordinatesForConnectivity(
        equipment: RawEquipmentNodeDto
    ): XyDto {
        return equipment.coords
    }

    private fun calculatePortCoordinatesForLibraryEquipment(
        equipment: RawEquipmentNodeDto,
        port: RawEquipmentNodeDto.PortDto
    ): XyDto {
        val libraryEquipment = EquipmentMetaInfoManager.getEquipmentLibById(equipment.libEquipmentId)
        val libraryPort = libraryEquipment.ports.first { it.libId == port.libId }
        val (x, y) = rotate(
            equipment.hour,
            equipment.coords.x + libraryEquipment.width[language]!! / 2,
            equipment.coords.y + libraryEquipment.height[language]!! / 2,
            equipment.coords.x + libraryPort.x[language]!!,
            equipment.coords.y + libraryPort.y[language]!!
        )
        return XyDto(x, y)
    }
}
