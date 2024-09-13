package ru.nti.dtps.cimconverter.fromcim.extractor.equipment

import ru.nti.dtps.cimconverter.fromcim.extensions.isBus
import ru.nti.dtps.cimconverter.fromcim.extensions.isTransmissionLineSegment
import ru.nti.dtps.cimconverter.fromcim.extractor.link.PortInfo
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.XyDto
import ru.nti.dtps.equipment.meta.info.dataclass.common.Language
import ru.nti.dtps.equipment.meta.info.dataclass.equipment.EquipmentLib
import ru.nti.dtps.equipment.meta.info.dataclass.equipment.port.Port
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.scheme.equipment.EquipmentNode
import java.util.*
import kotlin.math.abs

object PortsCreator {
    fun createPorts(
        equipmentId: String,
        equipmentLib: EquipmentLib,
        portsInfoFromCim: List<PortInfo>,
        equipmentCoords: XyDto
    ): List<RawEquipmentNodeDto.PortDto> {
        return if (equipmentLib.isBus()) {
            portsInfoFromCim.map { portFromCim ->
                RawEquipmentNodeDto.PortDto(
                    id = portFromCim.id,
                    alignment = EquipmentNode.Port.Alignment.BOTTOM,
                    parentNode = equipmentId,
                    links = portFromCim.linkIds,
                    libId = PortLibId.NO_ID,
                    coords = portFromCim.absoluteCoords?.let {
                        XyDto(
                            x = if (it.x == 0.0) 0.0 else abs(it.x - equipmentCoords.x),
                            y = if (it.y == 0.0) 0.0 else abs(it.y - equipmentCoords.y)
                        )
                    } ?: XyDto(0.0, 0.0)
                )
            }
        } else if (equipmentLib.isTransmissionLineSegment()) {
            portsInfoFromCim.mapNotNull { portFromCim ->
                findRelatedPortFromLibraryOrNull(equipmentLib, portFromCim)?.let { libPort ->
                    val points = portFromCim.transmissionLinePointsCoords?.map { coord ->
                        RawEquipmentNodeDto.PointDto(
                            id = UUID.randomUUID().toString(),
                            parentId = portFromCim.id,
                            coords = coord
                        )
                    } ?: emptyList()

                    RawEquipmentNodeDto.PortDto(
                        id = portFromCim.id,
                        alignment = libPort.alignment[Language.RU]!!,
                        parentNode = equipmentId,
                        links = portFromCim.linkIds,
                        points = points.let { if (it.size > 2) it else emptyList() },
                        libId = libPort.libId,
                        coords = XyDto(0.0, 0.0)
                    )
                }
            }
        } else {
            val updatedPortsInfoFromCim = if (portsInfoFromCim.any { it.sequenceNumber == null }) {
                portsInfoFromCim.mapIndexed { index, port -> port.copy(sequenceNumber = index + 1) }
            } else {
                portsInfoFromCim
            }

            val ports: List<RawEquipmentNodeDto.PortDto> = updatedPortsInfoFromCim.mapNotNull { portFromCim ->
                findRelatedPortFromLibraryOrNull(equipmentLib, portFromCim)?.let { libPort ->
                    RawEquipmentNodeDto.PortDto(
                        id = portFromCim.id,
                        alignment = libPort.alignment[Language.RU]!!,
                        parentNode = equipmentId,
                        links = portFromCim.linkIds,
                        libId = libPort.libId,
                        coords = XyDto(0.0, 0.0)
                    )
                }
            }

            ports + createMissedPortsIfNeed(equipmentId, equipmentLib, ports)
        }
    }

    private fun createMissedPortsIfNeed(
        equipmentId: String,
        equipmentLib: EquipmentLib,
        ports: List<RawEquipmentNodeDto.PortDto>
    ): List<RawEquipmentNodeDto.PortDto> {
        return if (ports.size != equipmentLib.ports.size) {
            equipmentLib.ports.mapNotNull { libPort ->
                if (!ports.any { it.libId == libPort.libId }) {
                    RawEquipmentNodeDto.PortDto(
                        id = UUID.randomUUID().toString(),
                        alignment = libPort.alignment[Language.RU]!!,
                        parentNode = equipmentId,
                        libId = libPort.libId,
                        links = mutableListOf(),
                        coords = XyDto(0.0, 0.0)
                    )
                } else {
                    null
                }
            }
        } else {
            listOf()
        }
    }

    private fun findRelatedPortFromLibraryOrNull(
        equipmentLib: EquipmentLib,
        portFromCim: PortInfo
    ): Port? {
        val portLibId = when (portFromCim.sequenceNumber!!) {
            1 -> PortLibId.FIRST
            2 -> PortLibId.SECOND
            3 -> PortLibId.THIRD
            4 -> PortLibId.FOURTH
            else -> null
        }

        return equipmentLib.ports.firstOrNull { it.libId == portLibId }
    }
}
