package ru.nti.dpts.schememanagerback.validator

import `in`.rcard.assertj.arrowcore.EitherAssert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.nti.dpts.schememanagerback.builder.EquipmentNodeBuilder
import ru.nti.dpts.schememanagerback.builder.SchemeBuilder
import ru.nti.dpts.schememanagerback.scheme.domain.TransmissionLine
import ru.nti.dpts.schememanagerback.scheme.service.validator.ValidatorService
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.TransmissionLineValidationError
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import ru.nti.dtps.proto.scheme.equipment.EquipmentNode
import java.util.*

@SpringBootTest(classes = [TestConfigurationToValidation::class])
class TransmissionLineDoubleCircuitTest {

    @Autowired
    lateinit var validatorService: ValidatorService

    private val NO_ID = "noId"
    private val transmissionLine1Id = UUID.randomUUID().toString()
    private val transmissionLine2Id = UUID.randomUUID().toString()
    private val powerSystemEquivalentId = UUID.randomUUID().toString()
    private val load1Id = UUID.randomUUID().toString()
    private val load2Id = UUID.randomUUID().toString()
    private val shortCircuitId = UUID.randomUUID().toString()
    private val transmissionLineDoubleCircuitId = UUID.randomUUID().toString()
    private val transmissionLineSegment1Id = UUID.randomUUID().toString()
    private val transmissionLineSegment2Id = UUID.randomUUID().toString()
    private val circuitBreakerId = UUID.randomUUID().toString()
    private val connectivityNodeId = UUID.randomUUID().toString()

    private val powerSystemEquivalentTemplate = EquipmentNodeBuilder()
        .buildPowerSystemEquivalent()
        .withId(powerSystemEquivalentId)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withSubstationId(NO_ID)

    private val load1Template = EquipmentNodeBuilder()
        .buildLoad()
        .withId(load1Id)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withFields(
            mapOf(
                FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to "110"
            )
        )
        .withSubstationId(NO_ID)

    private val load2Template = EquipmentNodeBuilder()
        .buildLoad()
        .withId(load2Id)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withFields(
            mapOf(
                FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to "110"
            )
        )
        .withSubstationId(NO_ID)

    private val shortCircuitTemplate = EquipmentNodeBuilder()
        .buildShortCircuit()
        .withId(shortCircuitId)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withSubstationId(NO_ID)

    private val transmissionLineDoubleCircuitTemplate = EquipmentNodeBuilder()
        .buildTransmissionLineDoubleSegment()
        .withId(transmissionLineDoubleCircuitId)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withFields(
            mapOf(
                FieldLibId.FIRST_CIRCUIT_TRANSMISSION_LINE to NO_ID,
                FieldLibId.SECOND_CIRCUIT_TRANSMISSION_LINE to NO_ID
            )
        )

    private val transmissionLineSegment1Template = EquipmentNodeBuilder()
        .buildTransmissionLineSegment()
        .withId(transmissionLineSegment1Id)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withFields(
            mapOf(
                FieldLibId.TRANSMISSION_LINE to NO_ID
            )
        )

    private val transmissionLineSegment2Template = EquipmentNodeBuilder()
        .buildTransmissionLineSegment()
        .withId(transmissionLineSegment2Id)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withFields(
            mapOf(
                FieldLibId.TRANSMISSION_LINE to NO_ID
            )
        )

    private val circuitBreakerTemplate = EquipmentNodeBuilder()
        .buildCircuitBreaker()
        .withId(circuitBreakerId)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withSubstationId(NO_ID)

    private val connectivityNodeTemplate = EquipmentNodeBuilder()
        .buildConnectivity()
        .withId(connectivityNodeId)

    private val transmissionLine1 = TransmissionLine(
        transmissionLine1Id,
        "Test"
    )

    private val transmissionLine2 = TransmissionLine(
        transmissionLine2Id,
        "Test 2"
    )

    @Test
    fun `valid one TLDC with same transmission line id`() {
        val linkIdFirstPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
        val linkIdSecondPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
        val linkIdThirdPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
        val linkIdFourthPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()

        val transmissionLineDoubleCircuit = transmissionLineDoubleCircuitTemplate
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(transmissionLineDoubleCircuitId)
                        .withLinks(listOf(linkIdFirstPortTransmissionLineDoubleCircuit))
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
                        .withLinks(listOf(linkIdThirdPortTransmissionLineDoubleCircuit))
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

        val powerSystemEquivalent = powerSystemEquivalentTemplate
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(powerSystemEquivalentId)
                        .withLinks(listOf(linkIdFirstPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val load1 = load1Template
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(load1Id)
                        .withLinks(listOf(linkIdSecondPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val load2 = load2Template
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(load2Id)
                        .withLinks(listOf(linkIdThirdPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val shortCircuit = shortCircuitTemplate
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(shortCircuitId)
                        .withLinks(listOf(linkIdFourthPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    transmissionLineDoubleCircuit.id to transmissionLineDoubleCircuit,
                    powerSystemEquivalent.id to powerSystemEquivalent,
                    load1.id to load1,
                    load2.id to load2,
                    shortCircuit.id to shortCircuit
                )

            )
            .connect(
                linkId = linkIdFirstPortTransmissionLineDoubleCircuit,
                sourceEquipment = powerSystemEquivalent,
                targetEquipment = transmissionLineDoubleCircuit,
                sourcePort = powerSystemEquivalent.ports[0],
                targetPort = transmissionLineDoubleCircuit.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdSecondPortTransmissionLineDoubleCircuit,
                sourceEquipment = transmissionLineDoubleCircuit,
                targetEquipment = load1,
                sourcePort = transmissionLineDoubleCircuit.ports[1],
                targetPort = load1.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdThirdPortTransmissionLineDoubleCircuit,
                sourceEquipment = load2,
                targetEquipment = transmissionLineDoubleCircuit,
                sourcePort = load2.ports[0],
                targetPort = transmissionLineDoubleCircuit.ports[2],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdFourthPortTransmissionLineDoubleCircuit,
                sourceEquipment = transmissionLineDoubleCircuit,
                targetEquipment = shortCircuit,
                sourcePort = transmissionLineDoubleCircuit.ports[3],
                targetPort = shortCircuit.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .build()

        Assertions.assertTrue(
            validatorService.validate(scheme).isRight()
        )
    }

    @Test
    fun `valid one TLDC with different transmission line id`() {
        val linkIdFirstPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
        val linkIdSecondPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
        val linkIdThirdPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
        val linkIdFourthPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()

        val transmissionLineDoubleCircuit = transmissionLineDoubleCircuitTemplate
            .withFields(
                mapOf(
                    FieldLibId.FIRST_CIRCUIT_TRANSMISSION_LINE to transmissionLine1.id,
                    FieldLibId.SECOND_CIRCUIT_TRANSMISSION_LINE to NO_ID
                )
            )
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(transmissionLineDoubleCircuitId)
                        .withLinks(listOf(linkIdFirstPortTransmissionLineDoubleCircuit))
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
                        .withLinks(listOf(linkIdThirdPortTransmissionLineDoubleCircuit))
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

        val powerSystemEquivalent = powerSystemEquivalentTemplate
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(powerSystemEquivalentId)
                        .withLinks(listOf(linkIdFirstPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val load1 = load1Template
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(load1Id)
                        .withLinks(listOf(linkIdSecondPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val load2 = load2Template
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(load2Id)
                        .withLinks(listOf(linkIdThirdPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val shortCircuit = shortCircuitTemplate
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(shortCircuitId)
                        .withLinks(listOf(linkIdFourthPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    transmissionLineDoubleCircuit.id to transmissionLineDoubleCircuit,
                    powerSystemEquivalent.id to powerSystemEquivalent,
                    load1.id to load1,
                    load2.id to load2,
                    shortCircuit.id to shortCircuit
                )

            )
            .connect(
                linkId = linkIdFirstPortTransmissionLineDoubleCircuit,
                sourceEquipment = powerSystemEquivalent,
                targetEquipment = transmissionLineDoubleCircuit,
                sourcePort = powerSystemEquivalent.ports[0],
                targetPort = transmissionLineDoubleCircuit.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdSecondPortTransmissionLineDoubleCircuit,
                sourceEquipment = transmissionLineDoubleCircuit,
                targetEquipment = load1,
                sourcePort = transmissionLineDoubleCircuit.ports[1],
                targetPort = load1.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdThirdPortTransmissionLineDoubleCircuit,
                sourceEquipment = load2,
                targetEquipment = transmissionLineDoubleCircuit,
                sourcePort = load2.ports[0],
                targetPort = transmissionLineDoubleCircuit.ports[2],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdFourthPortTransmissionLineDoubleCircuit,
                sourceEquipment = transmissionLineDoubleCircuit,
                targetEquipment = shortCircuit,
                sourcePort = transmissionLineDoubleCircuit.ports[3],
                targetPort = shortCircuit.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .withTransmissionLines(
                listOf(
                    transmissionLine1
                )
            )
            .build()

        Assertions.assertTrue(
            validatorService.validate(scheme).isRight()
        )
    }

    @Test
    fun `valid if one TLDC connect to two transmission line segments with different transmission line id`() {
        val linkIdFirstPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
        val linkIdSecondPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
        val linkIdThirdPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
        val linkIdFourthPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()

        val transmissionLineDoubleCircuit = transmissionLineDoubleCircuitTemplate
            .withFields(
                mapOf(
                    FieldLibId.FIRST_CIRCUIT_TRANSMISSION_LINE to transmissionLine1.id,
                    FieldLibId.SECOND_CIRCUIT_TRANSMISSION_LINE to transmissionLine2.id
                )
            )
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(transmissionLineDoubleCircuitId)
                        .withLinks(listOf(linkIdFirstPortTransmissionLineDoubleCircuit))
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
                        .withLinks(listOf(linkIdThirdPortTransmissionLineDoubleCircuit))
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

        val powerSystemEquivalent = powerSystemEquivalentTemplate
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(powerSystemEquivalentId)
                        .withLinks(listOf(linkIdFirstPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val linkIdSecondPortTransmissionLineSegment1 = UUID.randomUUID().toString()
        val transmissionLineSegment1 = transmissionLineSegment1Template
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(transmissionLineSegment1Id)
                        .withLinks(listOf(linkIdSecondPortTransmissionLineDoubleCircuit))
                        .build(),
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                        .withLibId(PortLibId.SECOND)
                        .withParentNode(transmissionLineSegment1Id)
                        .withLinks(listOf(linkIdSecondPortTransmissionLineSegment1))
                        .build()
                )
            )
            .build()

        val load1 = load1Template
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(load1Id)
                        .withLinks(listOf(linkIdSecondPortTransmissionLineSegment1))
                        .build()
                )
            )
            .build()

        val linkIdFirstPortTransmissionLineSegment2 = UUID.randomUUID().toString()
        val transmissionLineSegment2 = transmissionLineSegment2Template
            .withFields(
                mapOf(
                    FieldLibId.TRANSMISSION_LINE to transmissionLine2.id
                )
            )
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(transmissionLineSegment2Id)
                        .withLinks(listOf(linkIdFirstPortTransmissionLineSegment2))
                        .build(),
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                        .withLibId(PortLibId.SECOND)
                        .withParentNode(transmissionLineSegment2Id)
                        .withLinks(listOf(linkIdThirdPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val load2 = load2Template
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(load2Id)
                        .withLinks(listOf(linkIdFirstPortTransmissionLineSegment2))
                        .build()
                )
            )
            .build()

        val shortCircuit = shortCircuitTemplate
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(shortCircuitId)
                        .withLinks(listOf(linkIdFourthPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    transmissionLineDoubleCircuit.id to transmissionLineDoubleCircuit,
                    powerSystemEquivalent.id to powerSystemEquivalent,
                    load1.id to load1,
                    load2.id to load2,
                    shortCircuit.id to shortCircuit,
                    transmissionLineSegment1.id to transmissionLineSegment1,
                    transmissionLineSegment2.id to transmissionLineSegment2
                )

            )
            .connect(
                linkId = linkIdFirstPortTransmissionLineDoubleCircuit,
                sourceEquipment = powerSystemEquivalent,
                targetEquipment = transmissionLineDoubleCircuit,
                sourcePort = powerSystemEquivalent.ports[0],
                targetPort = transmissionLineDoubleCircuit.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdSecondPortTransmissionLineDoubleCircuit,
                sourceEquipment = transmissionLineDoubleCircuit,
                targetEquipment = transmissionLineSegment1,
                sourcePort = transmissionLineDoubleCircuit.ports[1],
                targetPort = transmissionLineSegment1.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdSecondPortTransmissionLineSegment1,
                sourceEquipment = transmissionLineSegment1,
                targetEquipment = load1,
                sourcePort = transmissionLineSegment1.ports[1],
                targetPort = load1.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdFirstPortTransmissionLineSegment2,
                sourceEquipment = load2,
                targetEquipment = transmissionLineSegment2,
                sourcePort = load2.ports[0],
                targetPort = transmissionLineSegment2.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdThirdPortTransmissionLineDoubleCircuit,
                sourceEquipment = transmissionLineSegment2,
                targetEquipment = transmissionLineDoubleCircuit,
                sourcePort = transmissionLineSegment2.ports[1],
                targetPort = transmissionLineDoubleCircuit.ports[2],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdFourthPortTransmissionLineDoubleCircuit,
                sourceEquipment = transmissionLineDoubleCircuit,
                targetEquipment = shortCircuit,
                sourcePort = transmissionLineDoubleCircuit.ports[3],
                targetPort = shortCircuit.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .withTransmissionLines(
                listOf(
                    transmissionLine1,
                    transmissionLine2
                )
            )
            .build()

        Assertions.assertTrue(
            validatorService.validate(scheme).isRight()
        )
    }

    @Test
    fun `has valid onLeft error if breaker locate between TLDC and transmission line segment with same transmission line id`() {
        val linkIdFirstPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
        val linkIdSecondPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
        val linkIdThirdPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
        val linkIdFourthPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()

        val transmissionLineDoubleCircuit = transmissionLineDoubleCircuitTemplate
            .withFields(
                mapOf(
                    FieldLibId.FIRST_CIRCUIT_TRANSMISSION_LINE to transmissionLine1.id,
                    FieldLibId.SECOND_CIRCUIT_TRANSMISSION_LINE to transmissionLine2.id
                )
            )
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(transmissionLineDoubleCircuitId)
                        .withLinks(listOf(linkIdFirstPortTransmissionLineDoubleCircuit))
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
                        .withLinks(listOf(linkIdThirdPortTransmissionLineDoubleCircuit))
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

        val powerSystemEquivalent = powerSystemEquivalentTemplate
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(powerSystemEquivalentId)
                        .withLinks(listOf(linkIdFirstPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val linkIdSecondPortCircuitBreaker = UUID.randomUUID().toString()
        val circuitBreaker = circuitBreakerTemplate
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(circuitBreakerId)
                        .withLinks(listOf(linkIdSecondPortTransmissionLineDoubleCircuit))
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

        val linkIdSecondPortTransmissionLineSegment1 = UUID.randomUUID().toString()
        val transmissionLineSegment1 = transmissionLineSegment1Template
            .withFields(
                mapOf(
                    FieldLibId.TRANSMISSION_LINE to transmissionLine1.id
                )
            )
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(transmissionLineSegment1Id)
                        .withLinks(listOf(linkIdSecondPortCircuitBreaker))
                        .build(),
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                        .withLibId(PortLibId.SECOND)
                        .withParentNode(transmissionLineSegment1Id)
                        .withLinks(listOf(linkIdSecondPortTransmissionLineSegment1))
                        .build()
                )
            )
            .build()

        val load1 = load1Template
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(load1Id)
                        .withLinks(listOf(linkIdSecondPortTransmissionLineSegment1))
                        .build()
                )
            )
            .build()

        val load2 = load2Template
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(load2Id)
                        .withLinks(listOf(linkIdThirdPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val shortCircuit = shortCircuitTemplate
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(shortCircuitId)
                        .withLinks(listOf(linkIdFourthPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    transmissionLineDoubleCircuit.id to transmissionLineDoubleCircuit,
                    powerSystemEquivalent.id to powerSystemEquivalent,
                    load1.id to load1,
                    load2.id to load2,
                    shortCircuit.id to shortCircuit,
                    transmissionLineSegment1.id to transmissionLineSegment1,
                    circuitBreaker.id to circuitBreaker
                )

            )
            .connect(
                linkId = linkIdFirstPortTransmissionLineDoubleCircuit,
                sourceEquipment = powerSystemEquivalent,
                targetEquipment = transmissionLineDoubleCircuit,
                sourcePort = powerSystemEquivalent.ports[0],
                targetPort = transmissionLineDoubleCircuit.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdSecondPortTransmissionLineDoubleCircuit,
                sourceEquipment = transmissionLineDoubleCircuit,
                targetEquipment = circuitBreaker,
                sourcePort = transmissionLineDoubleCircuit.ports[1],
                targetPort = circuitBreaker.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdSecondPortCircuitBreaker,
                sourceEquipment = circuitBreaker,
                targetEquipment = transmissionLineSegment1,
                sourcePort = circuitBreaker.ports[1],
                targetPort = transmissionLineSegment1.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdSecondPortTransmissionLineSegment1,
                sourceEquipment = transmissionLineSegment1,
                targetEquipment = load1,
                sourcePort = transmissionLineSegment1.ports[1],
                targetPort = load1.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdThirdPortTransmissionLineDoubleCircuit,
                sourceEquipment = load2,
                targetEquipment = transmissionLineDoubleCircuit,
                sourcePort = load2.ports[0],
                targetPort = transmissionLineDoubleCircuit.ports[2],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdFourthPortTransmissionLineDoubleCircuit,
                sourceEquipment = transmissionLineDoubleCircuit,
                targetEquipment = shortCircuit,
                sourcePort = transmissionLineDoubleCircuit.ports[3],
                targetPort = shortCircuit.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .withTransmissionLines(listOf(transmissionLine1, transmissionLine2))
            .build()

        EitherAssert.assertThat(
            validatorService.validate(scheme)
        ).containsLeftInstanceOf(
            TransmissionLineValidationError.TransmissionLineSegmentsDoesNotConnectedError::class.java
        )
    }

    @Test
    fun `valid if CN locate between TLDC and transmission line segment with same transmission line id`() {
        val linkIdFirstPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
        val linkIdSecondPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
        val linkIdThirdPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
        val linkIdFourthPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()

        val transmissionLineDoubleCircuit = transmissionLineDoubleCircuitTemplate
            .withFields(
                mapOf(
                    FieldLibId.FIRST_CIRCUIT_TRANSMISSION_LINE to transmissionLine1.id,
                    FieldLibId.SECOND_CIRCUIT_TRANSMISSION_LINE to transmissionLine2.id
                )
            )
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(transmissionLineDoubleCircuitId)
                        .withLinks(listOf(linkIdFirstPortTransmissionLineDoubleCircuit))
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
                        .withLinks(listOf(linkIdThirdPortTransmissionLineDoubleCircuit))
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

        val powerSystemEquivalent = powerSystemEquivalentTemplate
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(powerSystemEquivalentId)
                        .withLinks(listOf(linkIdFirstPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val shortCircuit2Id = UUID.randomUUID().toString()
        val linkIdShortCircuit = UUID.randomUUID().toString()
        val shortCircuit2 = EquipmentNodeBuilder()
            .buildShortCircuit()
            .withId(shortCircuit2Id)
            .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
            .withSubstationId(NO_ID)
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(shortCircuit2Id)
                        .withLinks(listOf(linkIdShortCircuit))
                        .build()
                )
            )
            .build()

        val linkIdFirstPortTransmissionLineSegment1 = UUID.randomUUID().toString()
        val linkIdSecondPortTransmissionLineSegment1 = UUID.randomUUID().toString()
        val transmissionLineSegment1 = transmissionLineSegment1Template
            .withFields(
                mapOf(
                    FieldLibId.TRANSMISSION_LINE to transmissionLine1.id
                )
            )
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(transmissionLineSegment1Id)
                        .withLinks(listOf(linkIdFirstPortTransmissionLineSegment1))
                        .build(),
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                        .withLibId(PortLibId.SECOND)
                        .withParentNode(transmissionLineSegment1Id)
                        .withLinks(listOf(linkIdSecondPortTransmissionLineSegment1))
                        .build()
                )
            )
            .build()

        val connectivity = connectivityNodeTemplate
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(connectivityNodeId)
                        .withLinks(
                            listOf(
                                linkIdSecondPortTransmissionLineDoubleCircuit,
                                linkIdShortCircuit,
                                linkIdFirstPortTransmissionLineSegment1
                            )
                        )
                        .build()
                )
            )
            .build()

        val load1 = load1Template
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(load1Id)
                        .withLinks(listOf(linkIdSecondPortTransmissionLineSegment1))
                        .build()
                )
            )
            .build()

        val load2 = load2Template
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(load2Id)
                        .withLinks(listOf(linkIdThirdPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val shortCircuit = shortCircuitTemplate
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(shortCircuitId)
                        .withLinks(listOf(linkIdFourthPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    transmissionLineDoubleCircuit.id to transmissionLineDoubleCircuit,
                    powerSystemEquivalent.id to powerSystemEquivalent,
                    load1.id to load1,
                    load2.id to load2,
                    shortCircuit.id to shortCircuit,
                    transmissionLineSegment1.id to transmissionLineSegment1,
                    shortCircuit2.id to shortCircuit2,
                    connectivity.id to connectivity
                )
            )
            .connect(
                linkId = linkIdFirstPortTransmissionLineDoubleCircuit,
                sourceEquipment = powerSystemEquivalent,
                targetEquipment = transmissionLineDoubleCircuit,
                sourcePort = powerSystemEquivalent.ports[0],
                targetPort = transmissionLineDoubleCircuit.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdSecondPortTransmissionLineDoubleCircuit,
                sourceEquipment = transmissionLineDoubleCircuit,
                targetEquipment = connectivity,
                sourcePort = transmissionLineDoubleCircuit.ports[1],
                targetPort = connectivity.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdShortCircuit,
                sourceEquipment = shortCircuit2,
                targetEquipment = connectivity,
                sourcePort = shortCircuit2.ports[0],
                targetPort = connectivity.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdFirstPortTransmissionLineSegment1,
                sourceEquipment = transmissionLineSegment1,
                targetEquipment = connectivity,
                sourcePort = transmissionLineSegment1.ports[0],
                targetPort = connectivity.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdSecondPortTransmissionLineSegment1,
                sourceEquipment = transmissionLineSegment1,
                targetEquipment = load1,
                sourcePort = transmissionLineSegment1.ports[1],
                targetPort = load1.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdThirdPortTransmissionLineDoubleCircuit,
                sourceEquipment = load2,
                targetEquipment = transmissionLineDoubleCircuit,
                sourcePort = load2.ports[0],
                targetPort = transmissionLineDoubleCircuit.ports[2],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdFourthPortTransmissionLineDoubleCircuit,
                sourceEquipment = transmissionLineDoubleCircuit,
                targetEquipment = shortCircuit,
                sourcePort = transmissionLineDoubleCircuit.ports[3],
                targetPort = shortCircuit.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .withTransmissionLines(listOf(transmissionLine1, transmissionLine2))
            .build()

        Assertions.assertTrue(
            validatorService.validate(scheme).isRight()
        )
    }

    @Test
    fun `has valid onLeft error if TLDC and connected transmission line segment have different voltage level`() {
        val linkIdFirstPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
        val linkIdSecondPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
        val linkIdThirdPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()
        val linkIdFourthPortTransmissionLineDoubleCircuit = UUID.randomUUID().toString()

        val transmissionLineDoubleCircuit = transmissionLineDoubleCircuitTemplate
            .withFields(
                mapOf(
                    FieldLibId.FIRST_CIRCUIT_TRANSMISSION_LINE to transmissionLine1.id,
                    FieldLibId.SECOND_CIRCUIT_TRANSMISSION_LINE to transmissionLine2.id
                )
            )
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(transmissionLineDoubleCircuitId)
                        .withLinks(listOf(linkIdFirstPortTransmissionLineDoubleCircuit))
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
                        .withLinks(listOf(linkIdThirdPortTransmissionLineDoubleCircuit))
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

        val powerSystemEquivalent = powerSystemEquivalentTemplate
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(powerSystemEquivalentId)
                        .withLinks(listOf(linkIdFirstPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val linkIdSecondPortTransmissionLineSegment1 = UUID.randomUUID().toString()
        val transmissionLineSegment1 = transmissionLineSegment1Template
            .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
            .withFields(
                mapOf(
                    FieldLibId.TRANSMISSION_LINE to transmissionLine1.id,
                    FieldLibId.VOLTAGE_LEVEL to "VOLTS_12"
                )
            )
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(transmissionLineSegment1Id)
                        .withLinks(listOf(linkIdSecondPortTransmissionLineDoubleCircuit))
                        .build(),
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                        .withLibId(PortLibId.SECOND)
                        .withParentNode(transmissionLineSegment1Id)
                        .withLinks(listOf(linkIdSecondPortTransmissionLineSegment1))
                        .build()
                )
            )
            .build()

        val load1 = load1Template
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(load1Id)
                        .withLinks(listOf(linkIdSecondPortTransmissionLineSegment1))
                        .build()
                )
            )
            .build()

        val load2 = load2Template
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(load2Id)
                        .withLinks(listOf(linkIdThirdPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val shortCircuit = shortCircuitTemplate
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(shortCircuitId)
                        .withLinks(listOf(linkIdFourthPortTransmissionLineDoubleCircuit))
                        .build()
                )
            )
            .build()

        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    transmissionLineDoubleCircuit.id to transmissionLineDoubleCircuit,
                    powerSystemEquivalent.id to powerSystemEquivalent,
                    load1.id to load1,
                    load2.id to load2,
                    shortCircuit.id to shortCircuit,
                    transmissionLineSegment1.id to transmissionLineSegment1
                )

            )
            .connect(
                linkId = linkIdFirstPortTransmissionLineDoubleCircuit,
                sourceEquipment = powerSystemEquivalent,
                targetEquipment = transmissionLineDoubleCircuit,
                sourcePort = powerSystemEquivalent.ports[0],
                targetPort = transmissionLineDoubleCircuit.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdSecondPortTransmissionLineDoubleCircuit,
                sourceEquipment = transmissionLineDoubleCircuit,
                targetEquipment = transmissionLineSegment1,
                sourcePort = transmissionLineDoubleCircuit.ports[1],
                targetPort = transmissionLineSegment1.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdSecondPortTransmissionLineSegment1,
                sourceEquipment = transmissionLineSegment1,
                targetEquipment = load1,
                sourcePort = transmissionLineSegment1.ports[1],
                targetPort = load1.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdThirdPortTransmissionLineDoubleCircuit,
                sourceEquipment = load2,
                targetEquipment = transmissionLineDoubleCircuit,
                sourcePort = load2.ports[0],
                targetPort = transmissionLineDoubleCircuit.ports[2],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdFourthPortTransmissionLineDoubleCircuit,
                sourceEquipment = transmissionLineDoubleCircuit,
                targetEquipment = shortCircuit,
                sourcePort = transmissionLineDoubleCircuit.ports[3],
                targetPort = shortCircuit.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .withTransmissionLines(
                listOf(
                    transmissionLine1,
                    transmissionLine2
                )
            )
            .build()

        EitherAssert.assertThat(
            validatorService.validate(scheme)
        ).containsLeftInstanceOf(
            TransmissionLineValidationError.TransmissionLineHasSegmentWithDifferentVoltages::class.java
        )
    }
}
