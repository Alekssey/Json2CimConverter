package ru.nti.dpts.schememanagerback.builder

import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.Substation
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import ru.nti.dtps.proto.scheme.equipment.EquipmentNode
import java.util.*

class SchemesForTest {

    companion object {

        fun buildSchemeWithAllThreePhaseEquipment(): SchemeDomain {
            val substation = SubstationBuilder().build()
            val transmissionLine = TransmissionLineBuilder().build()

            val busId = UUID.randomUUID().toString()
            val linkIdFirstPortBus = UUID.randomUUID().toString()
            val linkIdSecondPortBus = UUID.randomUUID().toString()
            val linkIdThirdPortBus = UUID.randomUUID().toString()
            val linkIdFourthPortBus = UUID.randomUUID().toString()
            val linkIdFifthPortBus = UUID.randomUUID().toString()
            val linkIdSixthPortBus = UUID.randomUUID().toString()
            val linkIdSeventhPortBus = UUID.randomUUID().toString()
            val linkIdEightPortBus = UUID.randomUUID().toString()
            val linkIdNinthPortBus = UUID.randomUUID().toString()
            val bus = EquipmentNodeBuilder()
                .buildBus()
                .withId(busId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdFirstPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdSecondPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdThirdPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdFourthPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdFifthPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdSixthPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdSeventhPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdEightPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdNinthPortBus))
                            .build()
                    )
                )
                .build()

            val transmissionLineDoubleCircuitId = UUID.randomUUID().toString()
            val linkIdSecondPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
            val linkIdFourthPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
            val transmissionLineDoubleCircuit = EquipmentNodeBuilder()
                .buildTransmissionLineDoubleSegment()
                .withId(transmissionLineDoubleCircuitId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withFields(
                    mapOf(
                        FieldLibId.FIRST_CIRCUIT_TRANSMISSION_LINE to transmissionLine.id,
                        FieldLibId.SECOND_CIRCUIT_TRANSMISSION_LINE to transmissionLine.id
                    )
                )
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(transmissionLineDoubleCircuitId)
                            .withLinks(listOf(linkIdFourthPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(transmissionLineDoubleCircuitId)
                            .withLinks(listOf(linkIdSecondPortTransmissionLineDoubleCircuit))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.THIRD)
                            .withParentNode(transmissionLineDoubleCircuitId)
                            .withLinks(listOf(linkIdFifthPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.FOURTH)
                            .withParentNode(transmissionLineDoubleCircuitId)
                            .withLinks(listOf(linkIdFourthPortTransmissionLineDoubleCircuit))
                            .build()
                    )
                )
                .build()

            val synchronousGeneratorId = UUID.randomUUID().toString()
            val synchronousGenerator = EquipmentNodeBuilder()
                .buildSynchronousGenerator()
                .withId(synchronousGeneratorId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(synchronousGeneratorId)
                            .withLinks(listOf(linkIdFourthPortTransmissionLineDoubleCircuit))
                            .build()
                    )
                )
                .withFields(mapOf(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to "110"))
                .build()

            val lineSegmentId = UUID.randomUUID().toString()
            val linkIdSecondPortLineSegment = UUID.randomUUID().toString()
            val transmissionLineSegments = EquipmentNodeBuilder()
                .buildTransmissionLineSegment()
                .withId(lineSegmentId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withTransmissionLineId(transmissionLine.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(lineSegmentId)
                            .withLinks(listOf(linkIdSecondPortTransmissionLineDoubleCircuit))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(lineSegmentId)
                            .withLinks(listOf(linkIdSecondPortLineSegment))
                            .build()
                    )
                )
                .build()

            val twoWindingPowerTransformerId = UUID.randomUUID().toString()
            val linkIdFirstPortTwoWindingPowerTransformer = UUID.randomUUID().toString()
            val linkIdSecondPortTwoWindingPowerTransformer = UUID.randomUUID().toString()
            val twoWindingPowerTransformer = EquipmentNodeBuilder()
                .buildTwoWindingPowerTransformer()
                .withId(twoWindingPowerTransformerId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(twoWindingPowerTransformerId)
                            .withLinks(listOf(linkIdFirstPortTwoWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(twoWindingPowerTransformerId)
                            .withLinks(listOf(linkIdSecondPortTwoWindingPowerTransformer))
                            .build()
                    )
                )
                .build()

            val threeWindingPowerTransformerId = UUID.randomUUID().toString()
            val linkIdFirstPortThreeWindingPowerTransformer = UUID.randomUUID().toString()
            val linkIdSecondPortThreeWindingPowerTransformer = UUID.randomUUID().toString()
            val linkIdThirdPortThreeWindingPowerTransformer = UUID.randomUUID().toString()
            val threeWindingPowerTransformer = EquipmentNodeBuilder()
                .buildThreeWindingPowerTransformer()
                .withId(threeWindingPowerTransformerId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(threeWindingPowerTransformerId)
                            .withLinks(listOf(linkIdFirstPortThreeWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.RIGHT)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(threeWindingPowerTransformerId)
                            .withLinks(listOf(linkIdSecondPortThreeWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.THIRD)
                            .withParentNode(threeWindingPowerTransformerId)
                            .withLinks(listOf(linkIdThirdPortThreeWindingPowerTransformer))
                            .build()
                    )
                )
                .build()

            val twoWindingAutoTransformerId = UUID.randomUUID().toString()
            val linkIdSecondPortTwoWindingAutoTransformer = UUID.randomUUID().toString()
            val twoWindingAutoTransformer = EquipmentNodeBuilder()
                .buildTwoWindingAutoPowerTransformer()
                .withId(twoWindingAutoTransformerId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(twoWindingAutoTransformerId)
                            .withLinks(listOf(linkIdSixthPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(twoWindingAutoTransformerId)
                            .withLinks(listOf(linkIdSecondPortTwoWindingAutoTransformer))
                            .build()
                    )
                )
                .build()

            val threeWindingAutoTransformerId = UUID.randomUUID().toString()
            val linkIdSecondPortThreeWindingAutoTransformer = UUID.randomUUID().toString()
            val linkIdThirdPortThreeWindingAutoTransformer = UUID.randomUUID().toString()
            val threeWindingAutoTransformer = EquipmentNodeBuilder()
                .buildThreeWindingAutoPowerTransformer()
                .withId(threeWindingAutoTransformerId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(threeWindingAutoTransformerId)
                            .withLinks(listOf(linkIdSeventhPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.RIGHT)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(threeWindingAutoTransformerId)
                            .withLinks(listOf(linkIdSecondPortThreeWindingAutoTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.THIRD)
                            .withParentNode(threeWindingAutoTransformerId)
                            .withLinks(listOf(linkIdThirdPortThreeWindingAutoTransformer))
                            .build()
                    )
                )
                .build()

            val circuitBreakerId = UUID.randomUUID().toString()
            val linkIdSecondPortCircuitBreaker = UUID.randomUUID().toString()
            val circuitBreaker = EquipmentNodeBuilder()
                .buildCircuitBreaker()
                .withId(circuitBreakerId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_35)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(circuitBreakerId)
                            .withLinks(listOf(linkIdSecondPortThreeWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(circuitBreakerId)
                            .withLinks(listOf(linkIdSecondPortCircuitBreaker))
                            .build()
                    )
                )
                .build()

            val powerSystemId = UUID.randomUUID().toString()
            val powerSystemEquivalent = EquipmentNodeBuilder()
                .buildPowerSystemEquivalent()
                .withId(powerSystemId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_35)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(powerSystemId)
                            .withLinks(listOf(linkIdSecondPortCircuitBreaker))
                            .build()
                    )
                )
                .withFields(mapOf(FieldLibId.VOLTAGE_LINE_TO_LINE to "35"))
                .build()

            val loadId = UUID.randomUUID().toString()
            val load = EquipmentNodeBuilder()
                .buildLoad()
                .withId(loadId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(loadId)
                            .withLinks(listOf(linkIdThirdPortThreeWindingPowerTransformer))
                            .build()
                    )
                )
                .build()

            val asynchronousMotorId = UUID.randomUUID().toString()
            val asynchronousMotor = EquipmentNodeBuilder()
                .buildAsynchronousMotor()
                .withId(asynchronousMotorId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(asynchronousMotorId)
                            .withLinks(listOf(linkIdSecondPortBus))
                            .build()
                    )
                )
                .withFields(mapOf(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to "110"))
                .build()

            val shortCircuitId = UUID.randomUUID().toString()
            val shortCircuit = EquipmentNodeBuilder()
                .buildShortCircuit()
                .withId(shortCircuitId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(shortCircuitId)
                            .withLinks(listOf(linkIdSecondPortTwoWindingPowerTransformer))
                            .build()
                    )
                )
                .build()

            val groundingId = UUID.randomUUID().toString()
            val grounding = EquipmentNodeBuilder()
                .buildGrounding()
                .withId(groundingId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(groundingId)
                            .withLinks(listOf(linkIdThirdPortBus))
                            .build()
                    )
                )
                .build()

            val currentTransformerId = UUID.randomUUID().toString()
            val linkIdSecondPortCurrentTransformer = UUID.randomUUID().toString()
            val currentTransformer = EquipmentNodeBuilder()
                .buildCurrentTransformer()
                .withId(currentTransformerId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(currentTransformerId)
                            .withLinks(listOf(linkIdFirstPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(currentTransformerId)
                            .withLinks(listOf(linkIdSecondPortCurrentTransformer))
                            .build()
                    )
                )
                .build()

            val voltageTransformerId = UUID.randomUUID().toString()
            val linkIdFirstPortVoltageTransformer = UUID.randomUUID().toString()
            val voltageTransformer = EquipmentNodeBuilder()
                .buildVoltageTransformer()
                .withId(voltageTransformerId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(voltageTransformerId)
                            .withLinks(listOf(linkIdFirstPortVoltageTransformer))
                            .build()
                    )
                )
                .build()

            val resistanceId = UUID.randomUUID().toString()
            val resistance = EquipmentNodeBuilder()
                .buildResistance()
                .withId(resistanceId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(resistanceId)
                            .withLinks(listOf(linkIdSecondPortLineSegment))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(resistanceId)
                            .withLinks(listOf(linkIdFirstPortTwoWindingPowerTransformer))
                            .build()
                    )
                )
                .build()

            val connectivityId = UUID.randomUUID().toString()
            val connectivity = EquipmentNodeBuilder()
                .buildConnectivity()
                .withId(connectivityId)
                .withSubstationId("noId")
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(connectivityId)
                            .withLinks(
                                listOf(
                                    linkIdSecondPortCurrentTransformer,
                                    linkIdFirstPortVoltageTransformer,
                                    linkIdFirstPortThreeWindingPowerTransformer
                                )
                            )
                            .build()
                    )
                )
                .build()

            val skrmId = UUID.randomUUID().toString()
            val skrm = EquipmentNodeBuilder()
                .buildReactivePowerCompSystem()
                .withId(skrmId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_35)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(skrmId)
                            .withLinks(listOf(linkIdSecondPortThreeWindingAutoTransformer))
                            .build()
                    )
                )
                .withFields(mapOf(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to "35"))
                .build()

            val load2Id = UUID.randomUUID().toString()
            val load2 = EquipmentNodeBuilder()
                .buildLoad()
                .withId(load2Id)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(load2Id)
                            .withLinks(listOf(linkIdThirdPortThreeWindingAutoTransformer))
                            .build()
                    )
                )
                .withFields(mapOf(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to "10"))
                .build()

            val inductanceId = UUID.randomUUID().toString()
            val linkIdSecondPortInductance = UUID.randomUUID().toString()
            val inductance = EquipmentNodeBuilder()
                .buildInductance()
                .withId(inductanceId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(inductanceId)
                            .withLinks(listOf(linkIdSecondPortTwoWindingAutoTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(inductanceId)
                            .withLinks(listOf(linkIdSecondPortInductance))
                            .build()
                    )
                )
                .build()

            val capacitanceId = UUID.randomUUID().toString()
            val linkIdSecondPortCapacitance = UUID.randomUUID().toString()
            val capacitance = EquipmentNodeBuilder()
                .buildCapacitance()
                .withId(capacitanceId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(capacitanceId)
                            .withLinks(listOf(linkIdSecondPortInductance))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(capacitanceId)
                            .withLinks(listOf(linkIdSecondPortCapacitance))
                            .build()
                    )
                )
                .build()

            val load3Id = UUID.randomUUID().toString()
            val load3 = EquipmentNodeBuilder()
                .buildLoad()
                .withId(load3Id)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(load3Id)
                            .withLinks(listOf(linkIdSecondPortCapacitance))
                            .build()
                    )
                )
                .build()

            val currentSourceDcId = UUID.randomUUID().toString()
            val linkIdSecondPortCurrentSourceDc = UUID.randomUUID().toString()
            val currentSourceDc = EquipmentNodeBuilder()
                .buildCurrentSourceDC()
                .withId(currentSourceDcId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(currentSourceDcId)
                            .withLinks(listOf(linkIdEightPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(currentSourceDcId)
                            .withLinks(listOf(linkIdSecondPortCurrentSourceDc))
                            .build()
                    )
                )
                .build()

            val sourceOfElectromotiveForceDcId = UUID.randomUUID().toString()
            val linkIdSecondPortSourceOfElectromotiveForceDc = UUID.randomUUID().toString()
            val sourceOfElectromotiveForce = EquipmentNodeBuilder()
                .buildSourceOfElectromotiveForceDC()
                .withId(sourceOfElectromotiveForceDcId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(sourceOfElectromotiveForceDcId)
                            .withLinks(listOf(linkIdSecondPortCurrentSourceDc))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(sourceOfElectromotiveForceDcId)
                            .withLinks(listOf(linkIdSecondPortSourceOfElectromotiveForceDc))
                            .build()
                    )
                )
                .withFields(mapOf(FieldLibId.ELECTROMOTIVE_FORCE_PHASE_A to "115"))
                .build()

            val electricityStorageId = UUID.randomUUID().toString()
            val electricityStorage = EquipmentNodeBuilder()
                .buildElectricityStorage()
                .withId(electricityStorageId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(electricityStorageId)
                            .withLinks(listOf(linkIdSecondPortSourceOfElectromotiveForceDc))
                            .build()
                    )
                )
                .build()

            val disconnectorId = UUID.randomUUID().toString()
            val linkIdSecondPortDisconnector = UUID.randomUUID().toString()
            val disconnector = EquipmentNodeBuilder()
                .buildDisconnector()
                .withId(disconnectorId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(disconnectorId)
                            .withLinks(listOf(linkIdNinthPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(disconnectorId)
                            .withLinks(listOf(linkIdSecondPortDisconnector))
                            .build()
                    )
                )
                .build()

            val groundDisconnectorId = UUID.randomUUID().toString()
            val groundDisconnector = EquipmentNodeBuilder()
                .buildGroundDisconnector()
                .withId(groundDisconnectorId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(groundDisconnectorId)
                            .withLinks(listOf(linkIdSecondPortDisconnector))
                            .build()
                    )
                )
                .build()

            val nodes = mapOf(
                bus.id to bus,
                transmissionLineSegments.id to transmissionLineSegments,
                powerSystemEquivalent.id to powerSystemEquivalent,
                circuitBreaker.id to circuitBreaker,
                twoWindingPowerTransformer.id to twoWindingPowerTransformer,
                threeWindingPowerTransformer.id to threeWindingPowerTransformer,
                load.id to load,
                asynchronousMotor.id to asynchronousMotor,
                shortCircuit.id to shortCircuit,
                connectivity.id to connectivity,
                grounding.id to grounding,
                currentTransformer.id to currentTransformer,
                voltageTransformer.id to voltageTransformer,
                resistance.id to resistance,
                synchronousGenerator.id to synchronousGenerator,
                load2.id to load2,
                load3.id to load3,
                twoWindingAutoTransformer.id to twoWindingAutoTransformer,
                threeWindingAutoTransformer.id to threeWindingAutoTransformer,
                inductance.id to inductance,
                capacitance.id to capacitance,
                skrm.id to skrm,
                electricityStorage.id to electricityStorage,
                disconnector.id to disconnector,
                groundDisconnector.id to groundDisconnector,
                transmissionLineDoubleCircuit.id to transmissionLineDoubleCircuit,
                currentSourceDc.id to currentSourceDc,
                sourceOfElectromotiveForce.id to sourceOfElectromotiveForce
            )

            return SchemeBuilder()
                .withNodes(nodes)
                .connect(
                    linkId = linkIdFirstPortBus,
                    sourceEquipment = currentTransformer,
                    targetEquipment = bus,
                    sourcePort = currentTransformer.ports[0],
                    targetPort = bus.ports[0],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortBus,
                    sourceEquipment = asynchronousMotor,
                    targetEquipment = bus,
                    sourcePort = asynchronousMotor.ports[0],
                    targetPort = bus.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdThirdPortBus,
                    sourceEquipment = grounding,
                    targetEquipment = bus,
                    sourcePort = grounding.ports[0],
                    targetPort = bus.ports[2],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdFourthPortBus,
                    sourceEquipment = transmissionLineDoubleCircuit,
                    targetEquipment = bus,
                    sourcePort = transmissionLineDoubleCircuit.ports[0],
                    targetPort = bus.ports[3],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortTransmissionLineDoubleCircuit,
                    sourceEquipment = transmissionLineDoubleCircuit,
                    targetEquipment = transmissionLineSegments,
                    sourcePort = transmissionLineDoubleCircuit.ports[1],
                    targetPort = transmissionLineSegments.ports[0],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdFifthPortBus,
                    sourceEquipment = transmissionLineDoubleCircuit,
                    targetEquipment = bus,
                    sourcePort = transmissionLineDoubleCircuit.ports[2],
                    targetPort = bus.ports[4],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdFourthPortTransmissionLineDoubleCircuit,
                    sourceEquipment = transmissionLineDoubleCircuit,
                    targetEquipment = synchronousGenerator,
                    sourcePort = transmissionLineDoubleCircuit.ports[3],
                    targetPort = synchronousGenerator.ports[0],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortLineSegment,
                    sourceEquipment = resistance,
                    targetEquipment = transmissionLineSegments,
                    sourcePort = resistance.ports[0],
                    targetPort = transmissionLineSegments.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdFirstPortTwoWindingPowerTransformer,
                    sourceEquipment = twoWindingPowerTransformer,
                    targetEquipment = resistance,
                    sourcePort = twoWindingPowerTransformer.ports[0],
                    targetPort = resistance.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortTwoWindingPowerTransformer,
                    sourceEquipment = shortCircuit,
                    targetEquipment = twoWindingPowerTransformer,
                    sourcePort = shortCircuit.ports[0],
                    targetPort = twoWindingPowerTransformer.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortCurrentTransformer,
                    sourceEquipment = currentTransformer,
                    targetEquipment = connectivity,
                    sourcePort = currentTransformer.ports[1],
                    targetPort = connectivity.ports[0],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdFirstPortVoltageTransformer,
                    sourceEquipment = voltageTransformer,
                    targetEquipment = connectivity,
                    sourcePort = voltageTransformer.ports[0],
                    targetPort = connectivity.ports[0],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdFirstPortThreeWindingPowerTransformer,
                    sourceEquipment = threeWindingPowerTransformer,
                    targetEquipment = connectivity,
                    sourcePort = threeWindingPowerTransformer.ports[0],
                    targetPort = connectivity.ports[0],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdThirdPortThreeWindingPowerTransformer,
                    sourceEquipment = load,
                    targetEquipment = threeWindingPowerTransformer,
                    sourcePort = load.ports[0],
                    targetPort = threeWindingPowerTransformer.ports[2],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
                )
                .connect(
                    linkId = linkIdSecondPortThreeWindingPowerTransformer,
                    sourceEquipment = circuitBreaker,
                    targetEquipment = threeWindingPowerTransformer,
                    sourcePort = circuitBreaker.ports[0],
                    targetPort = threeWindingPowerTransformer.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_35
                )
                .connect(
                    linkId = linkIdSecondPortCircuitBreaker,
                    sourceEquipment = powerSystemEquivalent,
                    targetEquipment = circuitBreaker,
                    sourcePort = powerSystemEquivalent.ports[0],
                    targetPort = circuitBreaker.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_35
                )
                .connect(
                    linkId = linkIdSixthPortBus,
                    sourceEquipment = twoWindingAutoTransformer,
                    targetEquipment = bus,
                    sourcePort = twoWindingAutoTransformer.ports[0],
                    targetPort = bus.ports[5],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortTwoWindingAutoTransformer,
                    sourceEquipment = inductance,
                    targetEquipment = twoWindingAutoTransformer,
                    sourcePort = inductance.ports[0],
                    targetPort = twoWindingAutoTransformer.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
                )
                .connect(
                    linkId = linkIdSecondPortInductance,
                    sourceEquipment = capacitance,
                    targetEquipment = inductance,
                    sourcePort = capacitance.ports[0],
                    targetPort = inductance.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
                )
                .connect(
                    linkId = linkIdSecondPortCapacitance,
                    sourceEquipment = load3,
                    targetEquipment = capacitance,
                    sourcePort = load3.ports[0],
                    targetPort = capacitance.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
                )
                .connect(
                    linkId = linkIdSeventhPortBus,
                    sourceEquipment = threeWindingAutoTransformer,
                    targetEquipment = bus,
                    sourcePort = threeWindingAutoTransformer.ports[0],
                    targetPort = bus.ports[6],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortThreeWindingAutoTransformer,
                    sourceEquipment = skrm,
                    targetEquipment = threeWindingAutoTransformer,
                    sourcePort = skrm.ports[0],
                    targetPort = threeWindingAutoTransformer.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_35
                )
                .connect(
                    linkId = linkIdThirdPortThreeWindingAutoTransformer,
                    sourceEquipment = load2,
                    targetEquipment = threeWindingAutoTransformer,
                    sourcePort = load2.ports[0],
                    targetPort = threeWindingAutoTransformer.ports[2],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
                )
                .connect(
                    linkId = linkIdEightPortBus,
                    sourceEquipment = currentSourceDc,
                    targetEquipment = bus,
                    sourcePort = currentSourceDc.ports[0],
                    targetPort = bus.ports[7],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortCurrentSourceDc,
                    sourceEquipment = sourceOfElectromotiveForce,
                    targetEquipment = currentSourceDc,
                    sourcePort = sourceOfElectromotiveForce.ports[0],
                    targetPort = currentSourceDc.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortSourceOfElectromotiveForceDc,
                    sourceEquipment = sourceOfElectromotiveForce,
                    targetEquipment = electricityStorage,
                    sourcePort = sourceOfElectromotiveForce.ports[1],
                    targetPort = electricityStorage.ports[0],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdNinthPortBus,
                    sourceEquipment = disconnector,
                    targetEquipment = bus,
                    sourcePort = disconnector.ports[0],
                    targetPort = bus.ports[8],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortDisconnector,
                    sourceEquipment = disconnector,
                    targetEquipment = groundDisconnector,
                    sourcePort = disconnector.ports[1],
                    targetPort = groundDisconnector.ports[0],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .withSubstations(listOf(substation))
                .withTransmissionLines(listOf(transmissionLine))
                .build()
        }

        fun buildSchemeWithAllTypesEquipment(): SchemeDomain {
            val substation = SubstationBuilder().build()
            val transmissionLine = TransmissionLineBuilder().build()

            val busId = UUID.randomUUID().toString()
            val linkIdFirstPortBus = UUID.randomUUID().toString()
            val linkIdSecondPortBus = UUID.randomUUID().toString()
            val linkIdThirdPortBus = UUID.randomUUID().toString()
            val linkIdFourthPortBus = UUID.randomUUID().toString()
            val linkIdFifthPortBus = UUID.randomUUID().toString()
            val linkIdSixthPortBus = UUID.randomUUID().toString()
            val linkIdSeventhPortBus = UUID.randomUUID().toString()
            val linkIdEighthPortBus = UUID.randomUUID().toString()
            val linkIdNinthPortBus = UUID.randomUUID().toString()
            val linkIdTenthPortBus = UUID.randomUUID().toString()
            val bus = EquipmentNodeBuilder()
                .buildBus()
                .withId(busId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdFirstPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdSecondPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdThirdPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdFourthPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdFifthPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdSixthPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdSeventhPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdEighthPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdNinthPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdTenthPortBus))
                            .build()
                    )
                )
                .build()

            val transmissionLineDoubleCircuitId = UUID.randomUUID().toString()
            val linkIdSecondPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
            val linkIdFourthPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
            val transmissionLineDoubleCircuit = EquipmentNodeBuilder()
                .buildTransmissionLineDoubleSegment()
                .withId(transmissionLineDoubleCircuitId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withFields(
                    mapOf(
                        FieldLibId.FIRST_CIRCUIT_TRANSMISSION_LINE to transmissionLine.id,
                        FieldLibId.SECOND_CIRCUIT_TRANSMISSION_LINE to transmissionLine.id
                    )
                )
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(transmissionLineDoubleCircuitId)
                            .withLinks(listOf(linkIdFourthPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(transmissionLineDoubleCircuitId)
                            .withLinks(listOf(linkIdSecondPortTransmissionLineDoubleCircuit))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.THIRD)
                            .withParentNode(transmissionLineDoubleCircuitId)
                            .withLinks(listOf(linkIdFifthPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.FOURTH)
                            .withParentNode(transmissionLineDoubleCircuitId)
                            .withLinks(listOf(linkIdFourthPortTransmissionLineDoubleCircuit))
                            .build()
                    )
                )
                .build()

            val synchronousGeneratorId = UUID.randomUUID().toString()
            val synchronousGenerator = EquipmentNodeBuilder()
                .buildSynchronousGenerator()
                .withId(synchronousGeneratorId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(synchronousGeneratorId)
                            .withLinks(listOf(linkIdFourthPortTransmissionLineDoubleCircuit))
                            .build()
                    )
                )
                .withFields(mapOf(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to "110"))
                .build()

            val lineSegmentId = UUID.randomUUID().toString()
            val linkIdSecondPortLineSegment = UUID.randomUUID().toString()
            val transmissionLineSegments = EquipmentNodeBuilder()
                .buildTransmissionLineSegment()
                .withId(lineSegmentId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withTransmissionLineId(transmissionLine.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(lineSegmentId)
                            .withLinks(listOf(linkIdSecondPortTransmissionLineDoubleCircuit))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(lineSegmentId)
                            .withLinks(listOf(linkIdSecondPortLineSegment))
                            .build()
                    )
                )
                .build()

            val twoWindingPowerTransformerId = UUID.randomUUID().toString()
            val linkIdFirstPortTwoWindingPowerTransformer = UUID.randomUUID().toString()
            val linkIdSecondPortTwoWindingPowerTransformer = UUID.randomUUID().toString()
            val twoWindingPowerTransformer = EquipmentNodeBuilder()
                .buildTwoWindingPowerTransformer()
                .withId(twoWindingPowerTransformerId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(twoWindingPowerTransformerId)
                            .withLinks(listOf(linkIdFirstPortTwoWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(twoWindingPowerTransformerId)
                            .withLinks(listOf(linkIdSecondPortTwoWindingPowerTransformer))
                            .build()
                    )
                )
                .build()

            val threeWindingPowerTransformerId = UUID.randomUUID().toString()
            val linkIdFirstPortThreeWindingPowerTransformer = UUID.randomUUID().toString()
            val linkIdSecondPortThreeWindingPowerTransformer = UUID.randomUUID().toString()
            val linkIdThirdPortThreeWindingPowerTransformer = UUID.randomUUID().toString()
            val threeWindingPowerTransformer = EquipmentNodeBuilder()
                .buildThreeWindingPowerTransformer()
                .withId(threeWindingPowerTransformerId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(threeWindingPowerTransformerId)
                            .withLinks(listOf(linkIdFirstPortThreeWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.RIGHT)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(threeWindingPowerTransformerId)
                            .withLinks(listOf(linkIdSecondPortThreeWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.THIRD)
                            .withParentNode(threeWindingPowerTransformerId)
                            .withLinks(listOf(linkIdThirdPortThreeWindingPowerTransformer))
                            .build()
                    )
                )
                .build()

            val twoWindingAutoTransformerId = UUID.randomUUID().toString()
            val linkIdSecondPortTwoWindingAutoTransformer = UUID.randomUUID().toString()
            val twoWindingAutoTransformer = EquipmentNodeBuilder()
                .buildTwoWindingAutoPowerTransformer()
                .withId(twoWindingAutoTransformerId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(twoWindingAutoTransformerId)
                            .withLinks(listOf(linkIdSixthPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(twoWindingAutoTransformerId)
                            .withLinks(listOf(linkIdSecondPortTwoWindingAutoTransformer))
                            .build()
                    )
                )
                .build()

            val threeWindingAutoTransformerId = UUID.randomUUID().toString()
            val linkIdSecondPortThreeWindingAutoTransformer = UUID.randomUUID().toString()
            val linkIdThirdPortThreeWindingAutoTransformer = UUID.randomUUID().toString()
            val threeWindingAutoTransformer = EquipmentNodeBuilder()
                .buildThreeWindingAutoPowerTransformer()
                .withId(threeWindingAutoTransformerId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(threeWindingAutoTransformerId)
                            .withLinks(listOf(linkIdSeventhPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.RIGHT)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(threeWindingAutoTransformerId)
                            .withLinks(listOf(linkIdSecondPortThreeWindingAutoTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.THIRD)
                            .withParentNode(threeWindingAutoTransformerId)
                            .withLinks(listOf(linkIdThirdPortThreeWindingAutoTransformer))
                            .build()
                    )
                )
                .build()

            val circuitBreakerId = UUID.randomUUID().toString()
            val linkIdSecondPortCircuitBreaker = UUID.randomUUID().toString()
            val circuitBreaker = EquipmentNodeBuilder()
                .buildCircuitBreaker()
                .withId(circuitBreakerId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_35)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(circuitBreakerId)
                            .withLinks(listOf(linkIdSecondPortThreeWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(circuitBreakerId)
                            .withLinks(listOf(linkIdSecondPortCircuitBreaker))
                            .build()
                    )
                )
                .build()

            val powerSystemId = UUID.randomUUID().toString()
            val powerSystemEquivalent = EquipmentNodeBuilder()
                .buildPowerSystemEquivalent()
                .withId(powerSystemId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_35)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(powerSystemId)
                            .withLinks(listOf(linkIdSecondPortCircuitBreaker))
                            .build()
                    )
                )
                .withFields(mapOf(FieldLibId.VOLTAGE_LINE_TO_LINE to "35"))
                .build()

            val loadId = UUID.randomUUID().toString()
            val load = EquipmentNodeBuilder()
                .buildLoad()
                .withId(loadId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(loadId)
                            .withLinks(listOf(linkIdThirdPortThreeWindingPowerTransformer))
                            .build()
                    )
                )
                .build()

            val asynchronousMotorId = UUID.randomUUID().toString()
            val asynchronousMotor = EquipmentNodeBuilder()
                .buildAsynchronousMotor()
                .withId(asynchronousMotorId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(asynchronousMotorId)
                            .withLinks(listOf(linkIdSecondPortBus))
                            .build()
                    )
                )
                .withFields(mapOf(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to "110"))
                .build()

            val shortCircuitId = UUID.randomUUID().toString()
            val shortCircuit = EquipmentNodeBuilder()
                .buildShortCircuit()
                .withId(shortCircuitId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(shortCircuitId)
                            .withLinks(listOf(linkIdSecondPortTwoWindingPowerTransformer))
                            .build()
                    )
                )
                .build()

            val groundingId = UUID.randomUUID().toString()
            val grounding = EquipmentNodeBuilder()
                .buildGrounding()
                .withId(groundingId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(groundingId)
                            .withLinks(listOf(linkIdThirdPortBus))
                            .build()
                    )
                )
                .build()

            val currentTransformerId = UUID.randomUUID().toString()
            val linkIdSecondPortCurrentTransformer = UUID.randomUUID().toString()
            val currentTransformer = EquipmentNodeBuilder()
                .buildCurrentTransformer()
                .withId(currentTransformerId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(currentTransformerId)
                            .withLinks(listOf(linkIdFirstPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(currentTransformerId)
                            .withLinks(listOf(linkIdSecondPortCurrentTransformer))
                            .build()
                    )
                )
                .build()

            val voltageTransformerId = UUID.randomUUID().toString()
            val linkIdFirstPortVoltageTransformer = UUID.randomUUID().toString()
            val voltageTransformer = EquipmentNodeBuilder()
                .buildVoltageTransformer()
                .withId(voltageTransformerId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(voltageTransformerId)
                            .withLinks(listOf(linkIdFirstPortVoltageTransformer))
                            .build()
                    )
                )
                .build()

            val resistanceId = UUID.randomUUID().toString()
            val resistance = EquipmentNodeBuilder()
                .buildResistance()
                .withId(resistanceId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(resistanceId)
                            .withLinks(listOf(linkIdSecondPortLineSegment))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(resistanceId)
                            .withLinks(listOf(linkIdFirstPortTwoWindingPowerTransformer))
                            .build()
                    )
                )
                .build()

            val connectivityId = UUID.randomUUID().toString()
            val connectivity = EquipmentNodeBuilder()
                .buildConnectivity()
                .withId(connectivityId)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(connectivityId)
                            .withLinks(
                                listOf(
                                    linkIdSecondPortCurrentTransformer,
                                    linkIdFirstPortVoltageTransformer,
                                    linkIdFirstPortThreeWindingPowerTransformer
                                )
                            )
                            .build()
                    )
                )
                .build()

            val skrmId = UUID.randomUUID().toString()
            val skrm = EquipmentNodeBuilder()
                .buildReactivePowerCompSystem()
                .withId(skrmId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_35)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(skrmId)
                            .withLinks(listOf(linkIdSecondPortThreeWindingAutoTransformer))
                            .build()
                    )
                )
                .withFields(mapOf(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to "35"))
                .build()

            val load2Id = UUID.randomUUID().toString()
            val load2 = EquipmentNodeBuilder()
                .buildLoad()
                .withId(load2Id)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(load2Id)
                            .withLinks(listOf(linkIdThirdPortThreeWindingAutoTransformer))
                            .build()
                    )
                )
                .withFields(mapOf(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to "10"))
                .build()

            val inductanceId = UUID.randomUUID().toString()
            val linkIdSecondPortInductance = UUID.randomUUID().toString()
            val inductance = EquipmentNodeBuilder()
                .buildInductance()
                .withId(inductanceId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(inductanceId)
                            .withLinks(listOf(linkIdSecondPortTwoWindingAutoTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(inductanceId)
                            .withLinks(listOf(linkIdSecondPortInductance))
                            .build()
                    )
                )
                .build()

            val capacitanceId = UUID.randomUUID().toString()
            val linkIdSecondPortCapacitance = UUID.randomUUID().toString()
            val capacitance = EquipmentNodeBuilder()
                .buildCapacitance()
                .withId(capacitanceId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(capacitanceId)
                            .withLinks(listOf(linkIdSecondPortInductance))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(capacitanceId)
                            .withLinks(listOf(linkIdSecondPortCapacitance))
                            .build()
                    )
                )
                .build()

            val load3Id = UUID.randomUUID().toString()
            val load3 = EquipmentNodeBuilder()
                .buildLoad()
                .withId(load3Id)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(load3Id)
                            .withLinks(listOf(linkIdSecondPortCapacitance))
                            .build()
                    )
                )
                .build()

            val threePhaseConnectorId = UUID.randomUUID().toString()
            val linkIdSecondPortThreePhaseConnector = UUID.randomUUID().toString()
            val linkIdThirdPortThreePhaseConnector = UUID.randomUUID().toString()
            val linkIdFourthPortThreePhaseConnector = UUID.randomUUID().toString()
            val threePhaseConnector = EquipmentNodeBuilder()
                .buildThreePhaseConnector()
                .withId(threePhaseConnectorId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.LEFT)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(threePhaseConnectorId)
                            .withLinks(listOf(linkIdEighthPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.RIGHT)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(threePhaseConnectorId)
                            .withLinks(listOf(linkIdSecondPortThreePhaseConnector))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.RIGHT)
                            .withLibId(PortLibId.THIRD)
                            .withParentNode(threePhaseConnectorId)
                            .withLinks(listOf(linkIdThirdPortThreePhaseConnector))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.RIGHT)
                            .withLibId(PortLibId.FOURTH)
                            .withParentNode(threePhaseConnectorId)
                            .withLinks(listOf(linkIdFourthPortThreePhaseConnector))
                            .build()
                    )
                )
                .build()

            val currentSourceDC1PhId = UUID.randomUUID().toString()
            val linkIdSecondPortCurrentSourceDC1PhId = UUID.randomUUID().toString()
            val currentSourceDC1Ph = EquipmentNodeBuilder()
                .buildCurrentSourceDC1Ph()
                .withId(currentSourceDC1PhId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(currentSourceDC1PhId)
                            .withLinks(listOf(linkIdThirdPortThreePhaseConnector))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(currentSourceDC1PhId)
                            .withLinks(listOf(linkIdSecondPortCurrentSourceDC1PhId))
                            .build()
                    )
                )
                .build()

            val inductance1PhId = UUID.randomUUID().toString()
            val linkIdSecondPortInductance1Ph = UUID.randomUUID().toString()
            val inductance1Ph = EquipmentNodeBuilder()
                .buildInductance1Ph()
                .withId(inductance1PhId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(inductance1PhId)
                            .withLinks(listOf(linkIdSecondPortCurrentSourceDC1PhId))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(inductance1PhId)
                            .withLinks(listOf(linkIdSecondPortInductance1Ph))
                            .build()
                    )
                )
                .build()

            val capacitance1PhId = UUID.randomUUID().toString()
            val linkIdSecondPortCapacitance1Ph = UUID.randomUUID().toString()
            val capacitance1Ph = EquipmentNodeBuilder()
                .buildCapacitance1Ph()
                .withId(capacitance1PhId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(capacitance1PhId)
                            .withLinks(listOf(linkIdFourthPortThreePhaseConnector))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(capacitance1PhId)
                            .withLinks(listOf(linkIdSecondPortCapacitance1Ph))
                            .build()
                    )
                )
                .build()

            val sourceOfElectromotiveForceDC1PhId = UUID.randomUUID().toString()
            val linkIdSecondPortSourceOfElectromotiveForceDC1Ph = UUID.randomUUID().toString()
            val sourceOfElectromotiveForceDC1Ph = EquipmentNodeBuilder()
                .buildSourceOfElectromotiveForceDC1Ph()
                .withId(sourceOfElectromotiveForceDC1PhId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(sourceOfElectromotiveForceDC1PhId)
                            .withLinks(listOf(linkIdSecondPortThreePhaseConnector))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(sourceOfElectromotiveForceDC1PhId)
                            .withLinks(listOf(linkIdSecondPortSourceOfElectromotiveForceDC1Ph))
                            .build()
                    )
                )
                .build()

            val resistance1PhId = UUID.randomUUID().toString()
            val linkIdSecondPortResistance1Ph = UUID.randomUUID().toString()
            val resistance1Ph = EquipmentNodeBuilder()
                .buildResistance1Ph()
                .withId(resistance1PhId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(resistance1PhId)
                            .withLinks(listOf(linkIdSecondPortSourceOfElectromotiveForceDC1Ph))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(resistance1PhId)
                            .withLinks(listOf(linkIdSecondPortResistance1Ph))
                            .build()
                    )
                )
                .build()

            val circuitBreaker1PhId = UUID.randomUUID().toString()
            val linkIdSecondPortCircuitBreaker1Ph = UUID.randomUUID().toString()
            val circuitBreaker1Ph = EquipmentNodeBuilder()
                .buildCircuitBreaker1Ph()
                .withId(circuitBreaker1PhId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(circuitBreaker1PhId)
                            .withLinks(listOf(linkIdSecondPortCapacitance1Ph))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(circuitBreaker1PhId)
                            .withLinks(listOf(linkIdSecondPortCircuitBreaker1Ph))
                            .build()
                    )
                )
                .build()

            val shortCircuit1PhId = UUID.randomUUID().toString()
            val shortCircuit1Ph = EquipmentNodeBuilder()
                .buildShortCircuit1Ph()
                .withId(shortCircuit1PhId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(shortCircuit1PhId)
                            .withLinks(listOf(linkIdSecondPortCircuitBreaker1Ph))
                            .build()
                    )
                )
                .build()

            val grounding1PhId1 = UUID.randomUUID().toString()
            val grounding1Ph1 = EquipmentNodeBuilder()
                .buildGrounding1Ph()
                .withId(grounding1PhId1)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(grounding1PhId1)
                            .withLinks(listOf(linkIdSecondPortResistance1Ph))
                            .build()
                    )
                )
                .build()

            val disconnector1PhId = UUID.randomUUID().toString()
            val linkIdSecondPortDisconnector1Ph = UUID.randomUUID().toString()
            val disconnector1Ph = EquipmentNodeBuilder()
                .buildDisconnector1Ph()
                .withId(disconnector1PhId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(disconnector1PhId)
                            .withLinks(listOf(linkIdSecondPortInductance1Ph))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(disconnector1PhId)
                            .withLinks(listOf(linkIdSecondPortDisconnector1Ph))
                            .build()
                    )
                )
                .build()

            val groundDisconnector1PhId = UUID.randomUUID().toString()
            val groundDisconnector1Ph = EquipmentNodeBuilder()
                .buildGroundDisconnector1Ph()
                .withId(groundDisconnector1PhId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(groundDisconnector1PhId)
                            .withLinks(listOf(linkIdSecondPortDisconnector1Ph))
                            .build()
                    )
                )
                .build()

            val disconnectorId = UUID.randomUUID().toString()
            val linkIdSecondPortDisconnector = UUID.randomUUID().toString()
            val disconnector = EquipmentNodeBuilder()
                .buildDisconnector()
                .withId(disconnectorId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(disconnectorId)
                            .withLinks(listOf(linkIdTenthPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(disconnectorId)
                            .withLinks(listOf(linkIdSecondPortDisconnector))
                            .build()
                    )
                )
                .build()

            val groundDisconnectorId = UUID.randomUUID().toString()
            val groundDisconnector = EquipmentNodeBuilder()
                .buildGroundDisconnector()
                .withId(groundDisconnectorId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(groundDisconnectorId)
                            .withLinks(listOf(linkIdSecondPortDisconnector))
                            .build()
                    )
                )
                .build()

            val currentSourceDcId = UUID.randomUUID().toString()
            val linkIdSecondPortCurrentSourceDc = UUID.randomUUID().toString()
            val currentSourceDc = EquipmentNodeBuilder()
                .buildCurrentSourceDC()
                .withId(currentSourceDcId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(currentSourceDcId)
                            .withLinks(listOf(linkIdNinthPortBus))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(currentSourceDcId)
                            .withLinks(listOf(linkIdSecondPortCurrentSourceDc))
                            .build()
                    )
                )
                .build()

            val sourceOfElectromotiveForceDcId = UUID.randomUUID().toString()
            val linkIdSecondPortSourceOfElectromotiveForceDc = UUID.randomUUID().toString()
            val sourceOfElectromotiveForceDc = EquipmentNodeBuilder()
                .buildSourceOfElectromotiveForceDC()
                .withId(sourceOfElectromotiveForceDcId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(sourceOfElectromotiveForceDcId)
                            .withLinks(listOf(linkIdSecondPortCurrentSourceDc))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(sourceOfElectromotiveForceDcId)
                            .withLinks(listOf(linkIdSecondPortSourceOfElectromotiveForceDc))
                            .build()
                    )
                )
                .withFields(mapOf(FieldLibId.ELECTROMOTIVE_FORCE_PHASE_A to "115"))
                .build()

            val electricityStorageId = UUID.randomUUID().toString()
            val electricityStorage = EquipmentNodeBuilder()
                .buildElectricityStorage()
                .withId(electricityStorageId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(electricityStorageId)
                            .withLinks(listOf(linkIdSecondPortSourceOfElectromotiveForceDc))
                            .build()
                    )
                )
                .build()

            val nodes = mapOf(
                bus.id to bus,
                transmissionLineSegments.id to transmissionLineSegments,
                powerSystemEquivalent.id to powerSystemEquivalent,
                circuitBreaker.id to circuitBreaker,
                twoWindingPowerTransformer.id to twoWindingPowerTransformer,
                threeWindingPowerTransformer.id to threeWindingPowerTransformer,
                load.id to load,
                asynchronousMotor.id to asynchronousMotor,
                shortCircuit.id to shortCircuit,
                connectivity.id to connectivity,
                grounding.id to grounding,
                currentTransformer.id to currentTransformer,
                voltageTransformer.id to voltageTransformer,
                resistance.id to resistance,
                synchronousGenerator.id to synchronousGenerator,
                load2.id to load2,
                load3.id to load3,
                twoWindingAutoTransformer.id to twoWindingAutoTransformer,
                threeWindingAutoTransformer.id to threeWindingAutoTransformer,
                inductance.id to inductance,
                capacitance.id to capacitance,
                skrm.id to skrm,
                threePhaseConnector.id to threePhaseConnector,
                resistance1Ph.id to resistance1Ph,
                inductance1Ph.id to inductance1Ph,
                capacitance1Ph.id to capacitance1Ph,
                grounding1Ph1.id to grounding1Ph1,
                disconnector1Ph.id to disconnector1Ph,
                groundDisconnector1Ph.id to groundDisconnector1Ph,
                circuitBreaker1Ph.id to circuitBreaker1Ph,
                shortCircuit1Ph.id to shortCircuit1Ph,
                electricityStorage.id to electricityStorage,
                disconnector.id to disconnector,
                groundDisconnector.id to groundDisconnector,
                currentSourceDC1Ph.id to currentSourceDC1Ph,
                sourceOfElectromotiveForceDC1Ph.id to sourceOfElectromotiveForceDC1Ph,
                currentSourceDc.id to currentSourceDc,
                sourceOfElectromotiveForceDc.id to sourceOfElectromotiveForceDc,
                transmissionLineDoubleCircuit.id to transmissionLineDoubleCircuit
            )

            return SchemeBuilder()
                .withNodes(nodes)
                .connect(
                    linkId = linkIdFirstPortBus,
                    sourceEquipment = currentTransformer,
                    targetEquipment = bus,
                    sourcePort = currentTransformer.ports[0],
                    targetPort = bus.ports[0],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortBus,
                    sourceEquipment = asynchronousMotor,
                    targetEquipment = bus,
                    sourcePort = asynchronousMotor.ports[0],
                    targetPort = bus.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdThirdPortBus,
                    sourceEquipment = grounding,
                    targetEquipment = bus,
                    sourcePort = grounding.ports[0],
                    targetPort = bus.ports[2],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdFourthPortBus,
                    sourceEquipment = transmissionLineDoubleCircuit,
                    targetEquipment = bus,
                    sourcePort = transmissionLineDoubleCircuit.ports[0],
                    targetPort = bus.ports[3],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortTransmissionLineDoubleCircuit,
                    sourceEquipment = transmissionLineDoubleCircuit,
                    targetEquipment = transmissionLineSegments,
                    sourcePort = transmissionLineDoubleCircuit.ports[1],
                    targetPort = transmissionLineSegments.ports[0],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdFifthPortBus,
                    sourceEquipment = transmissionLineDoubleCircuit,
                    targetEquipment = bus,
                    sourcePort = transmissionLineDoubleCircuit.ports[2],
                    targetPort = bus.ports[4],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdFourthPortTransmissionLineDoubleCircuit,
                    sourceEquipment = transmissionLineDoubleCircuit,
                    targetEquipment = synchronousGenerator,
                    sourcePort = transmissionLineDoubleCircuit.ports[3],
                    targetPort = synchronousGenerator.ports[0],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortLineSegment,
                    sourceEquipment = resistance,
                    targetEquipment = transmissionLineSegments,
                    sourcePort = resistance.ports[0],
                    targetPort = transmissionLineSegments.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdFirstPortTwoWindingPowerTransformer,
                    sourceEquipment = twoWindingPowerTransformer,
                    targetEquipment = resistance,
                    sourcePort = twoWindingPowerTransformer.ports[0],
                    targetPort = resistance.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortTwoWindingPowerTransformer,
                    sourceEquipment = shortCircuit,
                    targetEquipment = twoWindingPowerTransformer,
                    sourcePort = shortCircuit.ports[0],
                    targetPort = twoWindingPowerTransformer.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortCurrentTransformer,
                    sourceEquipment = currentTransformer,
                    targetEquipment = connectivity,
                    sourcePort = currentTransformer.ports[1],
                    targetPort = connectivity.ports[0],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdFirstPortVoltageTransformer,
                    sourceEquipment = voltageTransformer,
                    targetEquipment = connectivity,
                    sourcePort = voltageTransformer.ports[0],
                    targetPort = connectivity.ports[0],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdFirstPortThreeWindingPowerTransformer,
                    sourceEquipment = threeWindingPowerTransformer,
                    targetEquipment = connectivity,
                    sourcePort = threeWindingPowerTransformer.ports[0],
                    targetPort = connectivity.ports[0],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdThirdPortThreeWindingPowerTransformer,
                    sourceEquipment = load,
                    targetEquipment = threeWindingPowerTransformer,
                    sourcePort = load.ports[0],
                    targetPort = threeWindingPowerTransformer.ports[2],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
                )
                .connect(
                    linkId = linkIdSecondPortThreeWindingPowerTransformer,
                    sourceEquipment = circuitBreaker,
                    targetEquipment = threeWindingPowerTransformer,
                    sourcePort = circuitBreaker.ports[0],
                    targetPort = threeWindingPowerTransformer.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_35
                )
                .connect(
                    linkId = linkIdSecondPortCircuitBreaker,
                    sourceEquipment = powerSystemEquivalent,
                    targetEquipment = circuitBreaker,
                    sourcePort = powerSystemEquivalent.ports[0],
                    targetPort = circuitBreaker.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_35
                )
                .connect(
                    linkId = linkIdSixthPortBus,
                    sourceEquipment = twoWindingAutoTransformer,
                    targetEquipment = bus,
                    sourcePort = twoWindingAutoTransformer.ports[0],
                    targetPort = bus.ports[5],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortTwoWindingAutoTransformer,
                    sourceEquipment = inductance,
                    targetEquipment = twoWindingAutoTransformer,
                    sourcePort = inductance.ports[0],
                    targetPort = twoWindingAutoTransformer.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
                )
                .connect(
                    linkId = linkIdSecondPortInductance,
                    sourceEquipment = capacitance,
                    targetEquipment = inductance,
                    sourcePort = capacitance.ports[0],
                    targetPort = inductance.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
                )
                .connect(
                    linkId = linkIdSecondPortCapacitance,
                    sourceEquipment = load3,
                    targetEquipment = capacitance,
                    sourcePort = load3.ports[0],
                    targetPort = capacitance.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
                )
                .connect(
                    linkId = linkIdSeventhPortBus,
                    sourceEquipment = threeWindingAutoTransformer,
                    targetEquipment = bus,
                    sourcePort = threeWindingAutoTransformer.ports[0],
                    targetPort = bus.ports[6],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortThreeWindingAutoTransformer,
                    sourceEquipment = skrm,
                    targetEquipment = threeWindingAutoTransformer,
                    sourcePort = skrm.ports[0],
                    targetPort = threeWindingAutoTransformer.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_35
                )
                .connect(
                    linkId = linkIdThirdPortThreeWindingAutoTransformer,
                    sourceEquipment = load2,
                    targetEquipment = threeWindingAutoTransformer,
                    sourcePort = load2.ports[0],
                    targetPort = threeWindingAutoTransformer.ports[2],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
                )
                .connect(
                    linkId = linkIdEighthPortBus,
                    sourceEquipment = threePhaseConnector,
                    targetEquipment = bus,
                    sourcePort = threePhaseConnector.ports[0],
                    targetPort = bus.ports[7],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortThreePhaseConnector,
                    sourceEquipment = sourceOfElectromotiveForceDC1Ph,
                    targetEquipment = threePhaseConnector,
                    sourcePort = sourceOfElectromotiveForceDC1Ph.ports[0],
                    targetPort = threePhaseConnector.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortSourceOfElectromotiveForceDC1Ph,
                    sourceEquipment = resistance1Ph,
                    targetEquipment = sourceOfElectromotiveForceDC1Ph,
                    sourcePort = resistance1Ph.ports[0],
                    targetPort = sourceOfElectromotiveForceDC1Ph.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdThirdPortThreePhaseConnector,
                    sourceEquipment = currentSourceDC1Ph,
                    targetEquipment = threePhaseConnector,
                    sourcePort = currentSourceDC1Ph.ports[0],
                    targetPort = threePhaseConnector.ports[2],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortCurrentSourceDC1PhId,
                    sourceEquipment = inductance1Ph,
                    targetEquipment = currentSourceDC1Ph,
                    sourcePort = inductance1Ph.ports[0],
                    targetPort = currentSourceDC1Ph.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdFourthPortThreePhaseConnector,
                    sourceEquipment = capacitance1Ph,
                    targetEquipment = threePhaseConnector,
                    sourcePort = capacitance1Ph.ports[0],
                    targetPort = threePhaseConnector.ports[3],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortResistance1Ph,
                    sourceEquipment = grounding1Ph1,
                    targetEquipment = resistance1Ph,
                    sourcePort = grounding1Ph1.ports[0],
                    targetPort = resistance1Ph.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortInductance1Ph,
                    sourceEquipment = disconnector1Ph,
                    targetEquipment = inductance1Ph,
                    sourcePort = disconnector1Ph.ports[0],
                    targetPort = inductance1Ph.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortDisconnector1Ph,
                    sourceEquipment = disconnector1Ph,
                    targetEquipment = groundDisconnector1Ph,
                    sourcePort = disconnector1Ph.ports[1],
                    targetPort = groundDisconnector1Ph.ports[0],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortCapacitance1Ph,
                    sourceEquipment = circuitBreaker1Ph,
                    targetEquipment = capacitance1Ph,
                    sourcePort = circuitBreaker1Ph.ports[0],
                    targetPort = capacitance1Ph.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortCircuitBreaker1Ph,
                    sourceEquipment = shortCircuit1Ph,
                    targetEquipment = circuitBreaker1Ph,
                    sourcePort = shortCircuit1Ph.ports[0],
                    targetPort = circuitBreaker1Ph.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdNinthPortBus,
                    sourceEquipment = currentSourceDc,
                    targetEquipment = bus,
                    sourcePort = currentSourceDc.ports[0],
                    targetPort = bus.ports[8],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortCurrentSourceDc,
                    sourceEquipment = sourceOfElectromotiveForceDc,
                    targetEquipment = currentSourceDc,
                    sourcePort = sourceOfElectromotiveForceDc.ports[0],
                    targetPort = currentSourceDc.ports[1],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortSourceOfElectromotiveForceDc,
                    sourceEquipment = sourceOfElectromotiveForceDc,
                    targetEquipment = electricityStorage,
                    sourcePort = sourceOfElectromotiveForceDc.ports[1],
                    targetPort = electricityStorage.ports[0],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdTenthPortBus,
                    sourceEquipment = disconnector,
                    targetEquipment = bus,
                    sourcePort = disconnector.ports[0],
                    targetPort = bus.ports[9],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .connect(
                    linkId = linkIdSecondPortDisconnector,
                    sourceEquipment = disconnector,
                    targetEquipment = groundDisconnector,
                    sourcePort = disconnector.ports[1],
                    targetPort = groundDisconnector.ports[0],
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .withSubstations(listOf(substation))
                .withTransmissionLines(listOf(transmissionLine))
                .build()
        }

        fun buildSchemeWithBusAndPowerSystemEquivalent(): SchemeDomain {
            val substation = SubstationBuilder().build()
            val transmissionLine = TransmissionLineBuilder().build()

            val busId = UUID.randomUUID().toString()
            val linkIdFirstPortBus = UUID.randomUUID().toString()
            val bus = EquipmentNodeBuilder()
                .buildBus()
                .withId(busId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.NO_ID)
                            .withParentNode(busId)
                            .withLinks(listOf(linkIdFirstPortBus))
                            .build()
                    )
                )
                .build()

            val powerSystemId = UUID.randomUUID().toString()
            val powerSystemEquivalent = EquipmentNodeBuilder()
                .buildPowerSystemEquivalent()
                .withId(powerSystemId)
                .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(powerSystemId)
                            .withLinks(listOf(linkIdFirstPortBus))
                            .build()
                    )
                )
                .build()

            val nodes = mapOf(
                bus.id to bus,
                powerSystemEquivalent.id to powerSystemEquivalent
            )

            return SchemeBuilder()
                .withNodes(nodes)
                .connect(
                    sourceEquipment = powerSystemEquivalent,
                    targetEquipment = bus,
                    sourcePort = powerSystemEquivalent.ports[0],
                    targetPort = bus.ports[0],
                    linkId = linkIdFirstPortBus,
                    voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
                )
                .withSubstations(listOf(substation))
                .withTransmissionLines(listOf(transmissionLine))
                .build()
        }

        fun buildSchemeWithThreeWindingPowerTransformerAndLoadOnLowVoltageSide(
            substation: Substation,
            targetEquipmentHighVoltageSide: ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain,
            targetPortHighVoltageSide: ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.PortDto,
            linkIdHighVoltageSide: String,
            targetEquipmentMiddleVoltageSide: ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain,
            targetPortMiddleVoltageSide: ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.PortDto,
            linkIdMiddleVoltageSide: String,
            highVoltageLevelLibId: VoltageLevelLibId,
            middleVoltageLevelLibId: VoltageLevelLibId,
            lowVoltageLevelLibId: VoltageLevelLibId
        ): SchemeBuilder {
            val threeWindingPowerTransformerId = UUID.randomUUID().toString()
            val linkIdFirstPortThreeWindingPowerTransformer = UUID.randomUUID().toString()
            val linkIdSecondPortThreeWindingPowerTransformer = UUID.randomUUID().toString()
            val linkIdThirdPortThreeWindingPowerTransformer = UUID.randomUUID().toString()
            val threeWindingPowerTransformer = EquipmentNodeBuilder()
                .buildThreeWindingPowerTransformer()
                .withId(threeWindingPowerTransformerId)
                .withVoltageLevelId(highVoltageLevelLibId)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(threeWindingPowerTransformerId)
                            .withLinks(listOf(linkIdFirstPortThreeWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.RIGHT)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(threeWindingPowerTransformerId)
                            .withLinks(listOf(linkIdSecondPortThreeWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.THIRD)
                            .withParentNode(threeWindingPowerTransformerId)
                            .withLinks(listOf(linkIdThirdPortThreeWindingPowerTransformer))
                            .build()
                    )
                )
                .build()

            val circuitBreakerHighSideId = UUID.randomUUID().toString()
            val circuitBreakerHighSide = EquipmentNodeBuilder()
                .buildCircuitBreaker()
                .withId(circuitBreakerHighSideId)
                .withVoltageLevelId(highVoltageLevelLibId)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(circuitBreakerHighSideId)
                            .withLinks(listOf(linkIdHighVoltageSide))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(circuitBreakerHighSideId)
                            .withLinks(listOf(linkIdFirstPortThreeWindingPowerTransformer))
                            .build()
                    )
                )
                .build()

            val circuitBreakerMiddleSideId = UUID.randomUUID().toString()
            val circuitBreakerMiddleSide = EquipmentNodeBuilder()
                .buildCircuitBreaker()
                .withId(circuitBreakerMiddleSideId)
                .withVoltageLevelId(middleVoltageLevelLibId)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(circuitBreakerMiddleSideId)
                            .withLinks(listOf(linkIdSecondPortThreeWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(circuitBreakerMiddleSideId)
                            .withLinks(listOf(linkIdMiddleVoltageSide))
                            .build()
                    )
                )
                .build()

            val circuitBreakerLowSideId = UUID.randomUUID().toString()
            val linkIdSecondPortCircuitBreakerLowSide = UUID.randomUUID().toString()
            val circuitBreakerLowSide = EquipmentNodeBuilder()
                .buildCircuitBreaker()
                .withId(circuitBreakerLowSideId)
                .withVoltageLevelId(lowVoltageLevelLibId)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(circuitBreakerLowSideId)
                            .withLinks(listOf(linkIdThirdPortThreeWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(circuitBreakerLowSideId)
                            .withLinks(listOf(linkIdSecondPortCircuitBreakerLowSide))
                            .build()
                    )
                )
                .build()

            val loadId = UUID.randomUUID().toString()
            val load = EquipmentNodeBuilder()
                .buildLoad()
                .withId(loadId)
                .withVoltageLevelId(lowVoltageLevelLibId)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(loadId)
                            .withLinks(listOf(linkIdSecondPortCircuitBreakerLowSide))
                            .build()
                    )
                )
                .build()

            val nodes = mapOf(
                circuitBreakerHighSide.id to circuitBreakerHighSide,
                circuitBreakerMiddleSide.id to circuitBreakerMiddleSide,
                circuitBreakerLowSide.id to circuitBreakerLowSide,
                threeWindingPowerTransformer.id to threeWindingPowerTransformer,
                load.id to load
            )

            return SchemeBuilder()
                .withNodes(nodes)
                .connect(
                    sourceEquipment = circuitBreakerHighSide,
                    sourcePort = circuitBreakerHighSide.ports[0],
                    targetEquipment = targetEquipmentHighVoltageSide,
                    targetPort = targetPortHighVoltageSide,
                    linkId = linkIdHighVoltageSide,
                    voltageLevelLibId = highVoltageLevelLibId
                )
                .connect(
                    sourceEquipment = threeWindingPowerTransformer,
                    sourcePort = threeWindingPowerTransformer.ports[0],
                    targetEquipment = circuitBreakerHighSide,
                    targetPort = circuitBreakerHighSide.ports[1],
                    linkId = linkIdFirstPortThreeWindingPowerTransformer,
                    voltageLevelLibId = highVoltageLevelLibId
                )
                .connect(
                    sourceEquipment = circuitBreakerMiddleSide,
                    sourcePort = circuitBreakerMiddleSide.ports[0],
                    targetEquipment = threeWindingPowerTransformer,
                    targetPort = threeWindingPowerTransformer.ports[1],
                    linkId = linkIdSecondPortThreeWindingPowerTransformer,
                    voltageLevelLibId = middleVoltageLevelLibId
                )
                .connect(
                    sourceEquipment = targetEquipmentMiddleVoltageSide,
                    sourcePort = targetPortMiddleVoltageSide,
                    targetEquipment = circuitBreakerMiddleSide,
                    targetPort = circuitBreakerMiddleSide.ports[1],
                    linkId = linkIdMiddleVoltageSide,
                    voltageLevelLibId = middleVoltageLevelLibId
                )
                .connect(
                    sourceEquipment = circuitBreakerLowSide,
                    sourcePort = circuitBreakerLowSide.ports[0],
                    targetEquipment = threeWindingPowerTransformer,
                    targetPort = threeWindingPowerTransformer.ports[2],
                    linkId = linkIdThirdPortThreeWindingPowerTransformer,
                    voltageLevelLibId = lowVoltageLevelLibId
                )
                .connect(
                    sourceEquipment = load,
                    sourcePort = load.ports[0],
                    targetEquipment = circuitBreakerLowSide,
                    targetPort = circuitBreakerLowSide.ports[1],
                    linkId = linkIdSecondPortCircuitBreakerLowSide,
                    voltageLevelLibId = lowVoltageLevelLibId
                )
        }

        fun buildSchemeWithThreeWindingPowerTransformerAndCircuitBreakers(
            targetEquipmentHighVoltageSide: ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain,
            targetPortHighVoltageSide: ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.PortDto,
            linkIdHighVoltageSide: String,
            targetEquipmentMiddleVoltageSide: ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain,
            targetPortMiddleVoltageSide: ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.PortDto,
            linkIdMiddleVoltageSide: String,
            targetEquipmentLowVoltageSide: ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain,
            targetPortLowVoltageSide: ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.PortDto,
            linkIdLowVoltageSide: String,
            highVoltageLevelLibId: VoltageLevelLibId,
            middleVoltageLevelLibId: VoltageLevelLibId,
            lowVoltageLevelLibId: VoltageLevelLibId,
            substation: Substation
        ): SchemeBuilder {
            val threeWindingPowerTransformerId = UUID.randomUUID().toString()
            val linkIdFirstPortThreeWindingPowerTransformer = UUID.randomUUID().toString()
            val linkIdSecondPortThreeWindingPowerTransformer = UUID.randomUUID().toString()
            val linkIdThirdPortThreeWindingPowerTransformer = UUID.randomUUID().toString()
            val threeWindingPowerTransformer = EquipmentNodeBuilder()
                .buildThreeWindingPowerTransformer()
                .withId(threeWindingPowerTransformerId)
                .withVoltageLevelId(highVoltageLevelLibId)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(threeWindingPowerTransformerId)
                            .withLinks(listOf(linkIdFirstPortThreeWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.RIGHT)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(threeWindingPowerTransformerId)
                            .withLinks(listOf(linkIdSecondPortThreeWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.THIRD)
                            .withParentNode(threeWindingPowerTransformerId)
                            .withLinks(listOf(linkIdThirdPortThreeWindingPowerTransformer))
                            .build()
                    )
                )
                .build()

            val circuitBreakerHighSideId = UUID.randomUUID().toString()
            val circuitBreakerHighSide = EquipmentNodeBuilder()
                .buildCircuitBreaker()
                .withId(circuitBreakerHighSideId)
                .withVoltageLevelId(highVoltageLevelLibId)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(circuitBreakerHighSideId)
                            .withLinks(listOf(linkIdHighVoltageSide))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(circuitBreakerHighSideId)
                            .withLinks(listOf(linkIdFirstPortThreeWindingPowerTransformer))
                            .build()
                    )
                )
                .build()

            val circuitBreakerMiddleSideId = UUID.randomUUID().toString()
            val circuitBreakerMiddleSide = EquipmentNodeBuilder()
                .buildCircuitBreaker()
                .withId(circuitBreakerMiddleSideId)
                .withVoltageLevelId(middleVoltageLevelLibId)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(circuitBreakerMiddleSideId)
                            .withLinks(listOf(linkIdSecondPortThreeWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(circuitBreakerMiddleSideId)
                            .withLinks(listOf(linkIdMiddleVoltageSide))
                            .build()
                    )
                )
                .build()

            val circuitBreakerLowSideId = UUID.randomUUID().toString()
            val circuitBreakerLowSide = EquipmentNodeBuilder()
                .buildCircuitBreaker()
                .withId(circuitBreakerLowSideId)
                .withVoltageLevelId(lowVoltageLevelLibId)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(circuitBreakerLowSideId)
                            .withLinks(listOf(linkIdThirdPortThreeWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(circuitBreakerLowSideId)
                            .withLinks(listOf(linkIdLowVoltageSide))
                            .build()
                    )
                )
                .build()

            val nodes = mapOf(
                circuitBreakerHighSide.id to circuitBreakerHighSide,
                circuitBreakerMiddleSide.id to circuitBreakerMiddleSide,
                circuitBreakerLowSide.id to circuitBreakerLowSide,
                threeWindingPowerTransformer.id to threeWindingPowerTransformer
            )

            return SchemeBuilder()
                .withNodes(nodes)
                .connect(
                    sourceEquipment = circuitBreakerHighSide,
                    sourcePort = circuitBreakerHighSide.ports[0],
                    targetEquipment = targetEquipmentHighVoltageSide,
                    targetPort = targetPortHighVoltageSide,
                    linkId = linkIdHighVoltageSide,
                    voltageLevelLibId = highVoltageLevelLibId
                )
                .connect(
                    sourceEquipment = threeWindingPowerTransformer,
                    sourcePort = threeWindingPowerTransformer.ports[0],
                    targetEquipment = circuitBreakerHighSide,
                    targetPort = circuitBreakerHighSide.ports[1],
                    linkId = linkIdFirstPortThreeWindingPowerTransformer,
                    voltageLevelLibId = highVoltageLevelLibId
                )
                .connect(
                    sourceEquipment = circuitBreakerMiddleSide,
                    sourcePort = circuitBreakerMiddleSide.ports[0],
                    targetEquipment = threeWindingPowerTransformer,
                    targetPort = threeWindingPowerTransformer.ports[1],
                    linkId = linkIdSecondPortThreeWindingPowerTransformer,
                    voltageLevelLibId = middleVoltageLevelLibId
                )
                .connect(
                    sourceEquipment = targetEquipmentMiddleVoltageSide,
                    sourcePort = targetPortMiddleVoltageSide,
                    targetEquipment = circuitBreakerMiddleSide,
                    targetPort = circuitBreakerMiddleSide.ports[1],
                    linkId = linkIdMiddleVoltageSide,
                    voltageLevelLibId = middleVoltageLevelLibId
                )
                .connect(
                    sourceEquipment = circuitBreakerLowSide,
                    sourcePort = circuitBreakerLowSide.ports[0],
                    targetEquipment = threeWindingPowerTransformer,
                    targetPort = threeWindingPowerTransformer.ports[2],
                    linkId = linkIdThirdPortThreeWindingPowerTransformer,
                    voltageLevelLibId = lowVoltageLevelLibId
                )
                .connect(
                    sourceEquipment = targetEquipmentLowVoltageSide,
                    sourcePort = targetPortLowVoltageSide,
                    targetEquipment = circuitBreakerLowSide,
                    targetPort = circuitBreakerLowSide.ports[1],
                    linkId = linkIdLowVoltageSide,
                    voltageLevelLibId = lowVoltageLevelLibId
                )
        }

        fun buildSchemeWithTwoWindingPowerTransformerAndLoadOnLowVoltageSide(
            substation: Substation,
            targetEquipment: ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain,
            targetPort: ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.PortDto,
            targetLinkId: String,
            highVoltageLevelLibId: VoltageLevelLibId,
            lowVoltageLevelLibId: VoltageLevelLibId
        ): SchemeBuilder {
            val twoWindingPowerTransformerId = UUID.randomUUID().toString()
            val linkIdFirstPortTwoWindingPowerTransformer = UUID.randomUUID().toString()
            val linkIdSecondPortTwoWindingPowerTransformer = UUID.randomUUID().toString()
            val twoWindingPowerTransformer = EquipmentNodeBuilder()
                .buildTwoWindingPowerTransformer()
                .withId(twoWindingPowerTransformerId)
                .withVoltageLevelId(highVoltageLevelLibId)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(twoWindingPowerTransformerId)
                            .withLinks(listOf(linkIdFirstPortTwoWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(twoWindingPowerTransformerId)
                            .withLinks(listOf(linkIdSecondPortTwoWindingPowerTransformer))
                            .build()
                    )
                )
                .build()

            val circuitBreakerHighSideId = UUID.randomUUID().toString()
            val circuitBreakerHighSide = EquipmentNodeBuilder()
                .buildCircuitBreaker()
                .withId(circuitBreakerHighSideId)
                .withVoltageLevelId(highVoltageLevelLibId)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(circuitBreakerHighSideId)
                            .withLinks(listOf(linkIdSecondPortTwoWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(circuitBreakerHighSideId)
                            .withLinks(listOf(targetLinkId))
                            .build()
                    )
                )
                .build()

            val circuitBreakerLowSideId = UUID.randomUUID().toString()
            val linkIdSecondPortCircuitBreakerLowSide = UUID.randomUUID().toString()
            val circuitBreakerLowSide = EquipmentNodeBuilder()
                .buildCircuitBreaker()
                .withId(circuitBreakerLowSideId)
                .withVoltageLevelId(lowVoltageLevelLibId)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(circuitBreakerLowSideId)
                            .withLinks(listOf(linkIdSecondPortTwoWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(circuitBreakerLowSideId)
                            .withLinks(listOf(linkIdSecondPortCircuitBreakerLowSide))
                            .build()
                    )
                )
                .build()

            val loadId = UUID.randomUUID().toString()
            val load = EquipmentNodeBuilder()
                .buildLoad()
                .withId(loadId)
                .withVoltageLevelId(lowVoltageLevelLibId)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(loadId)
                            .withLinks(listOf(linkIdSecondPortCircuitBreakerLowSide))
                            .build()
                    )
                )
                .build()

            val nodes = mapOf(
                twoWindingPowerTransformer.id to twoWindingPowerTransformer,
                circuitBreakerHighSide.id to circuitBreakerHighSide,
                circuitBreakerLowSide.id to circuitBreakerLowSide,
                load.id to load
            )

            return SchemeBuilder()
                .withNodes(nodes)
                .connect(
                    sourceEquipment = circuitBreakerHighSide,
                    sourcePort = circuitBreakerHighSide.ports[0],
                    targetEquipment = targetEquipment,
                    targetPort = targetPort,
                    linkId = targetLinkId,
                    voltageLevelLibId = highVoltageLevelLibId
                )
                .connect(
                    sourceEquipment = twoWindingPowerTransformer,
                    sourcePort = twoWindingPowerTransformer.ports[0],
                    targetEquipment = circuitBreakerHighSide,
                    targetPort = circuitBreakerHighSide.ports[1],
                    linkId = linkIdFirstPortTwoWindingPowerTransformer,
                    voltageLevelLibId = highVoltageLevelLibId
                )
                .connect(
                    sourceEquipment = circuitBreakerLowSide,
                    sourcePort = circuitBreakerLowSide.ports[0],
                    targetEquipment = twoWindingPowerTransformer,
                    targetPort = twoWindingPowerTransformer.ports[1],
                    linkId = linkIdSecondPortTwoWindingPowerTransformer,
                    voltageLevelLibId = lowVoltageLevelLibId
                )
                .connect(
                    sourceEquipment = load,
                    sourcePort = load.ports[0],
                    targetEquipment = circuitBreakerLowSide,
                    targetPort = circuitBreakerLowSide.ports[1],
                    linkId = linkIdSecondPortCircuitBreakerLowSide,
                    voltageLevelLibId = lowVoltageLevelLibId
                )
        }

        fun buildSchemeWithTwoWindingPowerTransformerAndCircuitBreakers(
            targetEquipmentHighVoltageSide: ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain,
            targetPortHighVoltageSide: ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.PortDto,
            linkIdHighVoltageSide: String,
            targetEquipmentLowVoltageSide: ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain,
            targetPortLowVoltageSide: ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain.PortDto,
            linkIdLowVoltageSide: String,
            highVoltageLevelLibId: VoltageLevelLibId,
            lowVoltageLevelLibId: VoltageLevelLibId,
            substation: Substation
        ): SchemeBuilder {
            val twoWindingPowerTransformerId = UUID.randomUUID().toString()
            val linkIdFirstPortTwoWindingPowerTransformer = UUID.randomUUID().toString()
            val linkIdSecondPortTwoWindingPowerTransformer = UUID.randomUUID().toString()
            val twoWindingPowerTransformer = EquipmentNodeBuilder()
                .buildTwoWindingPowerTransformer()
                .withId(twoWindingPowerTransformerId)
                .withVoltageLevelId(highVoltageLevelLibId)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(twoWindingPowerTransformerId)
                            .withLinks(listOf(linkIdFirstPortTwoWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(twoWindingPowerTransformerId)
                            .withLinks(listOf(linkIdSecondPortTwoWindingPowerTransformer))
                            .build()
                    )
                )
                .build()

            val circuitBreakerHighSideId = UUID.randomUUID().toString()
            val circuitBreakerHighSide = EquipmentNodeBuilder()
                .buildCircuitBreaker()
                .withId(circuitBreakerHighSideId)
                .withVoltageLevelId(highVoltageLevelLibId)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(circuitBreakerHighSideId)
                            .withLinks(listOf(linkIdSecondPortTwoWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(circuitBreakerHighSideId)
                            .withLinks(listOf(linkIdHighVoltageSide))
                            .build()
                    )
                )
                .build()

            val circuitBreakerLowSideId = UUID.randomUUID().toString()
            val circuitBreakerLowSide = EquipmentNodeBuilder()
                .buildCircuitBreaker()
                .withId(circuitBreakerLowSideId)
                .withVoltageLevelId(lowVoltageLevelLibId)
                .withSubstationId(substation.id)
                .withPorts(
                    listOf(
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.TOP)
                            .withLibId(PortLibId.FIRST)
                            .withParentNode(circuitBreakerLowSideId)
                            .withLinks(listOf(linkIdSecondPortTwoWindingPowerTransformer))
                            .build(),
                        EquipmentNodeBuilder.PortBuilder()
                            .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                            .withLibId(PortLibId.SECOND)
                            .withParentNode(circuitBreakerLowSideId)
                            .withLinks(listOf(linkIdLowVoltageSide))
                            .build()
                    )
                )
                .build()

            val nodes = mapOf(
                twoWindingPowerTransformer.id to twoWindingPowerTransformer,
                circuitBreakerLowSide.id to circuitBreakerLowSide
            )

            return SchemeBuilder()
                .withNodes(nodes)
                .connect(
                    sourceEquipment = circuitBreakerHighSide,
                    sourcePort = circuitBreakerHighSide.ports[0],
                    targetEquipment = targetEquipmentHighVoltageSide,
                    targetPort = targetPortHighVoltageSide,
                    linkId = linkIdHighVoltageSide,
                    voltageLevelLibId = highVoltageLevelLibId
                )
                .connect(
                    sourceEquipment = twoWindingPowerTransformer,
                    sourcePort = twoWindingPowerTransformer.ports[0],
                    targetEquipment = circuitBreakerHighSide,
                    targetPort = circuitBreakerHighSide.ports[1],
                    linkId = linkIdFirstPortTwoWindingPowerTransformer,
                    voltageLevelLibId = highVoltageLevelLibId
                )
                .connect(
                    sourceEquipment = circuitBreakerLowSide,
                    sourcePort = circuitBreakerLowSide.ports[0],
                    targetEquipment = twoWindingPowerTransformer,
                    targetPort = twoWindingPowerTransformer.ports[1],
                    linkId = linkIdSecondPortTwoWindingPowerTransformer,
                    voltageLevelLibId = lowVoltageLevelLibId
                )
                .connect(
                    sourceEquipment = targetEquipmentLowVoltageSide,
                    sourcePort = targetPortLowVoltageSide,
                    targetEquipment = circuitBreakerLowSide,
                    targetPort = circuitBreakerLowSide.ports[1],
                    linkId = linkIdLowVoltageSide,
                    voltageLevelLibId = lowVoltageLevelLibId
                )
        }
    }
}
