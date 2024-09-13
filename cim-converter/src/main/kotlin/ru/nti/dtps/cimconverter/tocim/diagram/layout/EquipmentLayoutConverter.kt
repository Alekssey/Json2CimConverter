package ru.nti.dtps.cimconverter.tocim.diagram.layout

import ru.nti.dtps.cimconverter.convertHourToDegrees
import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rotate
import ru.nti.dtps.dto.scheme.*
import java.util.*

object EquipmentLayoutConverter {

    fun createEquipmentRelatedDiagramObjectAndPoints(
        scheme: RawSchemeDto,
        equipments: List<RawEquipmentNodeDto>,
        diagram: RdfResource,
        equipmentIdToRdfResourceMap: Map<String, RdfResource>,
        portIdToTerminalResourceMap: Map<String, RdfResource>
    ): Pair<List<RdfResource>, List<RdfResource>> {
        val diagramObjects = mutableListOf<RdfResource>()
        val diagramObjectPoints = mutableListOf<RdfResource>()

        equipments.forEach { equipment ->
            val (diagramObject, equipmentDiagramObjectPoints) = createEquipmentDiagramObjectAndPoint(
                equipment,
                diagram,
                equipmentIdToRdfResourceMap
            )

            val (terminalDiagramObjects, terminalDiagramObjectPoints) = createEquipmentTerminalDiagramObjectAndPoint(
                scheme,
                equipment,
                diagram,
                portIdToTerminalResourceMap
            )

            diagramObjects += diagramObject
            diagramObjects += terminalDiagramObjects
            diagramObjectPoints += equipmentDiagramObjectPoints
            diagramObjectPoints += terminalDiagramObjectPoints
        }

        return diagramObjects to diagramObjectPoints
    }

    private fun createEquipmentDiagramObjectAndPoint(
        equipment: RawEquipmentNodeDto,
        diagram: RdfResource,
        equipmentIdToRdfResourceMap: Map<String, RdfResource>
    ): Pair<RdfResource, List<RdfResource>> {
        val equipmentDiagramObject = RdfResourceBuilder(UUID.randomUUID().toString(), CimClasses.DiagramObject)
            .addObjectProperty(CimClasses.DiagramObject.Diagram, diagram)
            .addObjectProperty(
                CimClasses.DiagramObject.IdentifiedObject,
                equipmentIdToRdfResourceMap[equipment.id]!!
            )
            .addDataProperty(CimClasses.DiagramObject.rotation, convertHourToDegrees(equipment.hour))
            .build()

        return equipmentDiagramObject to if (equipment.isBus()) {
            equipment.ports.mapIndexed { index, port ->
                val (x, y) = rotateBusPointsToAchieveAbsoluteCoords(
                    equipment,
                    equipment.coords.x + port.coords.x,
                    equipment.coords.y // y coordinate from bus port is always ignored
                )

                RdfResourceBuilder(
                    UUID.randomUUID().toString(),
                    CimClasses.DiagramObjectPoint
                )
                    .addObjectProperty(CimClasses.DiagramObjectPoint.DiagramObject, equipmentDiagramObject)
                    .addDataProperty(CimClasses.DiagramObjectPoint.sequenceNumber, index + 1)
                    .addDataProperty(CimClasses.DiagramObjectPoint.xPosition, x)
                    .addDataProperty(CimClasses.DiagramObjectPoint.yPosition, y)
                    .build()
            }
        } else {
            listOf(
                RdfResourceBuilder(
                    UUID.randomUUID().toString(),
                    CimClasses.DiagramObjectPoint
                )
                    .addObjectProperty(CimClasses.DiagramObjectPoint.DiagramObject, equipmentDiagramObject)
                    .addDataProperty(CimClasses.DiagramObjectPoint.sequenceNumber, "1")
                    .addDataProperty(CimClasses.DiagramObjectPoint.xPosition, equipment.coords.x)
                    .addDataProperty(CimClasses.DiagramObjectPoint.yPosition, equipment.coords.y)
                    .build()
            )
        }
    }

    private fun createEquipmentTerminalDiagramObjectAndPoint(
        scheme: RawSchemeDto,
        equipment: RawEquipmentNodeDto,
        diagram: RdfResource,
        portIdToTerminalResourceMap: Map<String, RdfResource>
    ): Pair<List<RdfResource>, List<RdfResource>> {
        return if (equipment.isBus()) {
            Pair(listOf(), listOf())
        } else if (equipment.isTransmissionLine()) {
            val diagramObjects = mutableListOf<RdfResource>()
            val diagramObjectPoints = mutableListOf<RdfResource>()

            equipment.ports.forEach { port ->
                if (port.points.isEmpty()) {
                    val (portDiagramObject, portDiagramObjectPoints) = createCommonEquipmentTerminalDiagramObjectAndPoint(
                        scheme,
                        diagram,
                        portIdToTerminalResourceMap,
                        port
                    )
                    diagramObjects += portDiagramObject
                    diagramObjectPoints += portDiagramObjectPoints
                } else {
                    val diagramObject = RdfResourceBuilder(UUID.randomUUID().toString(), CimClasses.DiagramObject)
                        .addObjectProperty(CimClasses.DiagramObject.Diagram, diagram)
                        .apply {
                            val terminalResource = portIdToTerminalResourceMap[port.id]
                            terminalResource?.let {
                                addObjectProperty(
                                    CimClasses.DiagramObject.IdentifiedObject,
                                    terminalResource
                                )
                            }
                        }
                        .build()

                    diagramObjects += diagramObject
                    diagramObjectPoints += createPointDiagramObjects(
                        port.points.map(RawEquipmentNodeDto.PointDto::coords),
                        diagramObject
                    )
                }
            }

            diagramObjects to diagramObjectPoints
        } else {
            val diagramObjects = mutableListOf<RdfResource>()
            val diagramObjectPoints = mutableListOf<RdfResource>()

            equipment.ports.forEach { port ->
                val (portDiagramObject, portDiagramObjectPoints) = createCommonEquipmentTerminalDiagramObjectAndPoint(
                    scheme,
                    diagram,
                    portIdToTerminalResourceMap,
                    port
                )
                diagramObjects += portDiagramObject
                diagramObjectPoints += portDiagramObjectPoints
            }

            diagramObjects to diagramObjectPoints
        }
    }

    private fun createCommonEquipmentTerminalDiagramObjectAndPoint(
        scheme: RawSchemeDto,
        diagram: RdfResource,
        portIdToTerminalResourceMap: Map<String, RdfResource>,
        port: RawEquipmentNodeDto.PortDto
    ): Pair<RdfResource, MutableList<RdfResource>> {
        val diagramObject = RdfResourceBuilder(UUID.randomUUID().toString(), CimClasses.DiagramObject)
            .addObjectProperty(CimClasses.DiagramObject.Diagram, diagram)
            .apply {
                val terminalResource = portIdToTerminalResourceMap[port.id]
                terminalResource?.let {
                    addObjectProperty(
                        CimClasses.DiagramObject.IdentifiedObject,
                        terminalResource
                    )
                }
            }.build()

        val diagramObjectPoints = mutableListOf<RdfResource>()

        port.getLinksFromScheme(scheme).firstOrNull()?.let { link ->
            val points = if (link.sourcePort == port.id) {
                link.points
            } else {
                link.points.reversed()
            }

            diagramObjectPoints += createPointDiagramObjects(
                points.map(RawEquipmentLinkDto.PointDto::coords),
                diagramObject
            )
        }

        return diagramObject to diagramObjectPoints
    }

    private fun createPointDiagramObjects(
        pointCoordsList: List<XyDto>,
        diagramObject: RdfResource
    ): List<RdfResource> = pointCoordsList.mapIndexed { pointIndex, pointCoords ->
        val sequenceNumber = pointIndex + 1
        RdfResourceBuilder(UUID.randomUUID().toString(), CimClasses.DiagramObjectPoint)
            .addObjectProperty(CimClasses.DiagramObjectPoint.DiagramObject, diagramObject)
            .addDataProperty(CimClasses.DiagramObjectPoint.sequenceNumber, sequenceNumber)
            .addDataProperty(CimClasses.DiagramObjectPoint.xPosition, pointCoords.x)
            .addDataProperty(CimClasses.DiagramObjectPoint.yPosition, pointCoords.y)
            .build()
    }

    private fun rotateBusPointsToAchieveAbsoluteCoords(
        equipment: RawEquipmentNodeDto,
        x: Double,
        y: Double
    ): Pair<Double, Double> {
        val (xCenter, yCenter) = equipment.coords.x + equipment.dimensions.width / 2 to equipment.coords.y
        return rotate(equipment.hour, xCenter, yCenter, x, y)
    }
}
