package ru.nti.dtps.cimconverter.fromcim.extractor.link

import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObject
import ru.nti.dtps.cimconverter.fromcim.extractor.diagram.layout.DiagramObjectPoint
import ru.nti.dtps.cimconverter.fromcim.extractor.terminal.Terminal
import ru.nti.dtps.dto.scheme.RawEquipmentNodeDto
import ru.nti.dtps.dto.scheme.XyDto
import ru.nti.dtps.equipment.meta.info.dataclass.common.Language
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.scheme.equipment.EquipmentNode

private val connectivityLib = EquipmentMetaInfoManager.getEquipmentLibById(EquipmentLibId.CONNECTIVITY)

object ConnectivityCreator {

    fun create(
        terminals: List<Terminal>,
        linkIds: List<String>,
        connectivityId: String,
        connectivityPortId: String,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>
    ) = RawEquipmentNodeDto(
        id = connectivityId,
        coords = calculateCoordsOfConnectivity(
            terminals,
            objectIdToDiagramObjectMap
        ),
        dimensions = createConnectivityDimensions(),
        libEquipmentId = EquipmentLibId.CONNECTIVITY,
        hour = 0,
        ports = listOf(
            RawEquipmentNodeDto.PortDto(
                id = connectivityPortId,
                alignment = EquipmentNode.Port.Alignment.TOP,
                parentNode = connectivityId,
                libId = PortLibId.FIRST,
                coords = XyDto(0.0, 0.0),
                links = linkIds
            )
        ),
        voltageLevelId = null
    )

    fun create(
        firstTerminal: Terminal,
        secondTerminal: Terminal,
        thirdTerminal: Terminal,
        firstLinkId: String,
        secondLinkId: String,
        thirdLinkId: String,
        connectivityId: String,
        connectivityPortId: String,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>
    ): RawEquipmentNodeDto {
        return create(
            listOf(firstTerminal, secondTerminal, thirdTerminal),
            listOf(firstLinkId, secondLinkId, thirdLinkId),
            connectivityId,
            connectivityPortId,
            objectIdToDiagramObjectMap
        )
    }

    private fun calculateCoordsOfConnectivity(
        terminals: List<Terminal>,
        objectIdToDiagramObjectMap: Map<String, DiagramObject>
    ): XyDto {
        val diagramObjects = terminals
            .mapNotNull { objectIdToDiagramObjectMap[it.id] }
            .ifEmpty { terminals.mapNotNull { objectIdToDiagramObjectMap[it.equipmentId] } }

        val (x, y) = if (diagramObjects.isEmpty()) {
            0.0 to 0.0
        } else {
            val points: List<DiagramObjectPoint> = diagramObjects.mapNotNull { it.points.lastOrNull() }
            val meanX = points.sumOf { it.coords.x } / points.size
            val meanY = points.sumOf { it.coords.y } / points.size
            meanX to meanY
        }

        return XyDto(
            x = x - connectivityLib.width[Language.RU]!! / 2,
            y = y - connectivityLib.height[Language.RU]!! / 2
        )
    }

    private fun createConnectivityDimensions() = RawEquipmentNodeDto.SizeDto(0.0, 0.0)
}
