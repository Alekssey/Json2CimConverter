package ru.nti.dpts.schememanagerback.validator

import `in`.rcard.assertj.arrowcore.EitherAssert
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.nti.dpts.schememanagerback.builder.*
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.TransmissionLineSegmentsValidator
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.TransmissionLineValidationError
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.VoltageValidationError
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.VoltagesValidator
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import ru.nti.dtps.proto.scheme.equipment.EquipmentNode
import java.util.*

@SpringBootTest(classes = [TestConfigurationToValidation::class])
class VoltagesValidatorTest {

    @Autowired
    lateinit var voltagesValidator: VoltagesValidator

    @Autowired
    lateinit var transmissionLineSegmentsValidator: TransmissionLineSegmentsValidator

    private val substation = SubstationBuilder().build()
    private val transmissionLine = TransmissionLineBuilder().build()

    private val busId = UUID.randomUUID().toString()

    private val powerSystemEquivalentId110 = UUID.randomUUID().toString()
    private val powerSystemEquivalentId500 = UUID.randomUUID().toString()

    private val twoWindingPowerTransformerId110 = UUID.randomUUID().toString()
    private val twoWindingPowerTransformerId220 = UUID.randomUUID().toString()

    private val lineSegmentId35 = UUID.randomUUID().toString()
    private val lineSegmentId110 = UUID.randomUUID().toString()

    private val synchronousGeneratorId500 = UUID.randomUUID().toString()
    private val asynchronousMotorId500 = UUID.randomUUID().toString()

    private val loadForTransformer110Id = UUID.randomUUID().toString()
    private val loadForTransformer220Id = UUID.randomUUID().toString()
    private val loadForThreeWindingPowerTransformerId = UUID.randomUUID().toString()
    private val loadForTransmissionLineSegment35Id = UUID.randomUUID().toString()
    private val loadForTransmissionLineSegment110Id = UUID.randomUUID().toString()
    private val loadForBusId = UUID.randomUUID().toString()

    private val linkIdFirstPortBus = UUID.randomUUID().toString()
    private val linkIdSecondPortBus = UUID.randomUUID().toString()
    private val linkIdThirdPortBus = UUID.randomUUID().toString()
    private val linkIdSecondPortTwoWindingPowerTransformer110 = UUID.randomUUID().toString()
    private val linkIdSecondPortTwoWindingPowerTransformer220 = UUID.randomUUID().toString()
    private val linkIdSecondPortLineSegment35 = UUID.randomUUID().toString()
    private val linkIdSecondPortLineSegment110 = UUID.randomUUID().toString()
    private val linkIdFirstPortLoad = UUID.randomUUID().toString()

    private val bus = EquipmentNodeBuilder().buildBus()
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
                    .build()
            )
        )
        .build()

    private val powerSystemEquivalent110 = EquipmentNodeBuilder()
        .buildPowerSystemEquivalent()
        .withId(powerSystemEquivalentId110)
        .withName("Power system equivalent 110")
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(powerSystemEquivalentId110)
                    .withLinks(listOf(linkIdFirstPortBus))
                    .build()
            )
        )
        .build()

    private val powerSystemEquivalent500 = EquipmentNodeBuilder()
        .buildPowerSystemEquivalent()
        .withId(powerSystemEquivalentId500)
        .withName("Power system equivalent 500")
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_500)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(powerSystemEquivalentId500)
                    .withLinks(listOf(linkIdSecondPortBus))
                    .build()
            )
        )
        .withFields(mapOf(FieldLibId.VOLTAGE_LINE_TO_LINE to "500"))
        .withSubstationId(substation.id)
        .build()

    private val twoWindingPowerTransformer110 = EquipmentNodeBuilder()
        .buildTwoWindingPowerTransformer()
        .withId(twoWindingPowerTransformerId110)
        .withName("T1_110")
        .withSubstationId(substation.id)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(twoWindingPowerTransformerId110)
                    .withLinks(listOf(linkIdSecondPortBus))
                    .build(),
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.RIGHT)
                    .withLibId(PortLibId.SECOND)
                    .withParentNode(twoWindingPowerTransformerId110)
                    .withLinks(listOf(linkIdSecondPortTwoWindingPowerTransformer110))
                    .build()
            )
        )
        .build()

    private val twoWindingPowerTransformer220 = EquipmentNodeBuilder()
        .buildTwoWindingPowerTransformer()
        .withId(twoWindingPowerTransformerId220)
        .withName("T1_220")
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_220)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(twoWindingPowerTransformerId220)
                    .withLinks(listOf(linkIdThirdPortBus))
                    .build(),
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.RIGHT)
                    .withLibId(PortLibId.SECOND)
                    .withParentNode(twoWindingPowerTransformerId220)
                    .withLinks(listOf(linkIdSecondPortTwoWindingPowerTransformer220))
                    .build()
            )
        )
        .withFields(mapOf(FieldLibId.FIRST_WINDING_RATED_VOLTAGE to "220"))
        .withSubstationId(substation.id)
        .build()

    private val transmissionLineSegment35 = EquipmentNodeBuilder()
        .buildTransmissionLineSegment()
        .withId(lineSegmentId35)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_35)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(lineSegmentId35)
                    .withLinks(listOf(linkIdThirdPortBus))
                    .build(),
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                    .withLibId(PortLibId.SECOND)
                    .withParentNode(lineSegmentId35)
                    .withLinks(listOf(linkIdSecondPortLineSegment35))
                    .build()
            )
        )
        .withFields(mapOf(FieldLibId.VOLTAGE_LEVEL to "KILOVOLTS_35"))
        .withTransmissionLineId(transmissionLine.id)
        .build()

    private val transmissionLineSegment110 = EquipmentNodeBuilder()
        .buildTransmissionLineSegment()
        .withId(lineSegmentId110)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withTransmissionLineId(transmissionLine.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(lineSegmentId110)
                    .withLinks(listOf(linkIdSecondPortLineSegment35))
                    .build(),
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                    .withLibId(PortLibId.SECOND)
                    .withParentNode(lineSegmentId35)
                    .withLinks(listOf(linkIdSecondPortLineSegment110))
                    .build()
            )
        )
        .build()

    private val loadForTransformer110 = EquipmentNodeBuilder()
        .buildLoad()
        .withId(loadForTransformer110Id)
        .withName("Load 1")
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(loadForTransformer110Id)
                    .withLinks(listOf(linkIdSecondPortTwoWindingPowerTransformer110))
                    .build()
            )
        )
        .build()

    private val loadForTransformer220 = EquipmentNodeBuilder()
        .buildLoad()
        .withId(loadForTransformer220Id)
        .withName("Load 1")
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(loadForTransformer220Id)
                    .withLinks(listOf(linkIdSecondPortTwoWindingPowerTransformer220))
                    .build()
            )
        )
        .build()

    private val loadForTransmissionLimeSegment35 = EquipmentNodeBuilder()
        .buildLoad()
        .withId(loadForTransmissionLineSegment35Id)
        .withName("Load 2")
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_35)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(loadForTransmissionLineSegment35Id)
                    .withLinks(listOf(linkIdSecondPortLineSegment35))
                    .build()
            )
        )
        .build()

    private val loadForTransmissionLimeSegment110 = EquipmentNodeBuilder()
        .buildLoad()
        .withId(loadForTransmissionLineSegment110Id)
        .withName("Load 4")
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(loadForTransmissionLineSegment110Id)
                    .withLinks(listOf(linkIdSecondPortLineSegment110))
                    .build()
            )
        )
        .build()

    private val loadForBus = EquipmentNodeBuilder()
        .buildLoad()
        .withId(loadForBusId)
        .withName("Load 3")
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_500)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(loadForBusId)
                    .withLinks(listOf(linkIdThirdPortBus))
                    .build()
            )
        )
        .withFields(mapOf(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to "500"))
        .withSubstationId(substation.id)
        .build()

    private val loadForThreeWindingPowerTransformer = EquipmentNodeBuilder()
        .buildLoad()
        .withId(loadForThreeWindingPowerTransformerId)
        .withName("Load 5")
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(loadForThreeWindingPowerTransformerId)
                    .withLinks(listOf(linkIdFirstPortLoad))
                    .build()
            )
        )
        .withFields(mapOf(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to "110"))
        .withSubstationId(substation.id)
        .build()

    private val synchronousGenerator = EquipmentNodeBuilder()
        .buildSynchronousGenerator()
        .withId(synchronousGeneratorId500)
        .withName("Synchronous generator 500")
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_500)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(synchronousGeneratorId500)
                    .withLinks(listOf(linkIdSecondPortBus))
                    .build()
            )
        )
        .withFields(mapOf(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to "500"))
        .withSubstationId(substation.id)
        .build()

    private val asynchronousMotor = EquipmentNodeBuilder()
        .buildAsynchronousMotor()
        .withId(asynchronousMotorId500)
        .withName("Asynchronous motor 500")
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_500)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(asynchronousMotorId500)
                    .withLinks(listOf(linkIdSecondPortBus))
                    .build()
            )
        )
        .withFields(mapOf(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to "500"))
        .withSubstationId(substation.id)
        .build()

    @Test
    fun `has valid onLeft error if transformer and transmission line has different voltage`() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    powerSystemEquivalent110.id to powerSystemEquivalent110,
                    transmissionLineSegment35.id to transmissionLineSegment35,
                    twoWindingPowerTransformer110.id to twoWindingPowerTransformer110,
                    loadForTransformer110.id to loadForTransformer110,
                    loadForTransmissionLimeSegment35.id to loadForTransmissionLimeSegment35
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent110,
                sourcePort = powerSystemEquivalent110.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[0],
                linkId = bus.ports[0].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                sourceEquipment = twoWindingPowerTransformer110,
                sourcePort = twoWindingPowerTransformer110.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[1],
                linkId = bus.ports[1].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                sourceEquipment = transmissionLineSegment35,
                sourcePort = transmissionLineSegment35.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[2],
                linkId = bus.ports[2].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_35
            )
            .connect(
                sourceEquipment = loadForTransformer110,
                sourcePort = loadForTransformer110.ports[0],
                targetEquipment = twoWindingPowerTransformer110,
                targetPort = twoWindingPowerTransformer110.ports[1],
                linkId = twoWindingPowerTransformer110.ports[1].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
            )
            .connect(
                sourceEquipment = loadForTransmissionLimeSegment35,
                sourcePort = loadForTransmissionLimeSegment35.ports[0],
                targetEquipment = transmissionLineSegment35,
                targetPort = transmissionLineSegment35.ports[1],
                linkId = transmissionLineSegment35.ports[1].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_35
            )
            .withSubstations(listOf(substation))
            .withTransmissionLines(listOf(transmissionLine))
            .build()

        EitherAssert.assertThat(
            voltagesValidator.validate(scheme)
        ).containsLeftInstanceOf(
            VoltageValidationError.ConnectedNodesHaveDifferentVoltageLevels::class.java
        )
    }

    @Test
    fun `has valid onLeft error if two transformers have different voltage`() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    powerSystemEquivalent110.id to powerSystemEquivalent110,
                    twoWindingPowerTransformer110.id to twoWindingPowerTransformer110,
                    twoWindingPowerTransformer220.id to twoWindingPowerTransformer220,
                    loadForTransformer110.id to loadForTransformer110,
                    loadForTransformer220.id to loadForTransformer220
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent110,
                sourcePort = powerSystemEquivalent110.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[0],
                linkId = bus.ports[0].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                sourceEquipment = twoWindingPowerTransformer110,
                sourcePort = twoWindingPowerTransformer110.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[1],
                linkId = bus.ports[1].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                sourceEquipment = twoWindingPowerTransformer220,
                sourcePort = twoWindingPowerTransformer220.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[2],
                linkId = bus.ports[2].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_220
            )
            .connect(
                sourceEquipment = loadForTransformer110,
                sourcePort = loadForTransformer110.ports[0],
                targetEquipment = twoWindingPowerTransformer110,
                targetPort = twoWindingPowerTransformer110.ports[1],
                linkId = twoWindingPowerTransformer110.ports[1].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
            )
            .connect(
                sourceEquipment = loadForTransformer220,
                sourcePort = loadForTransformer220.ports[0],
                targetEquipment = twoWindingPowerTransformer220,
                targetPort = twoWindingPowerTransformer220.ports[1],
                linkId = twoWindingPowerTransformer220.ports[1].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
            )
            .withSubstations(listOf(substation))
            .build()

        EitherAssert.assertThat(
            voltagesValidator.validate(scheme)
        ).containsLeftInstanceOf(
            VoltageValidationError.ConnectedNodesHaveDifferentVoltageLevels::class.java
        )
    }

    @Test
    fun `has valid onLeft error if transformer and power system have different voltage`() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    powerSystemEquivalent500.id to powerSystemEquivalent500,
                    twoWindingPowerTransformer110.id to twoWindingPowerTransformer110,
                    loadForTransformer110.id to loadForTransformer110
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent500,
                sourcePort = powerSystemEquivalent500.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[1],
                linkId = bus.ports[1].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_500
            )
            .connect(
                sourceEquipment = twoWindingPowerTransformer110,
                sourcePort = twoWindingPowerTransformer110.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[2],
                linkId = bus.ports[2].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                sourceEquipment = loadForTransformer110,
                sourcePort = loadForTransformer110.ports[0],
                targetEquipment = twoWindingPowerTransformer110,
                targetPort = twoWindingPowerTransformer110.ports[1],
                linkId = twoWindingPowerTransformer110.ports[1].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
            )
            .withSubstations(listOf(substation))
            .build()

        EitherAssert.assertThat(
            voltagesValidator.validate(scheme)
        ).containsLeftInstanceOf(
            VoltageValidationError.ConnectedNodesHaveDifferentVoltageLevels::class.java
        )
    }

    @Test
    fun `has valid onLeft error if transformer and static load have different voltage`() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    powerSystemEquivalent110.id to powerSystemEquivalent110,
                    twoWindingPowerTransformer110.id to twoWindingPowerTransformer110,
                    loadForTransformer110.id to loadForTransformer110,
                    loadForBus.id to loadForBus
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent110,
                sourcePort = powerSystemEquivalent110.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[0],
                linkId = bus.ports[0].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                sourceEquipment = twoWindingPowerTransformer110,
                sourcePort = twoWindingPowerTransformer110.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[1],
                linkId = bus.ports[1].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                sourceEquipment = loadForBus,
                sourcePort = loadForBus.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[2],
                linkId = bus.ports[2].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_500
            )
            .connect(
                sourceEquipment = loadForTransformer110,
                sourcePort = loadForTransformer110.ports[0],
                targetEquipment = twoWindingPowerTransformer110,
                targetPort = twoWindingPowerTransformer110.ports[1],
                linkId = twoWindingPowerTransformer110.ports[1].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
            )
            .withSubstations(listOf(substation))
            .build()

        EitherAssert.assertThat(
            voltagesValidator.validate(scheme)
        ).containsLeftInstanceOf(
            VoltageValidationError.ConnectedNodesHaveDifferentVoltageLevels::class.java
        )
    }

    @Test
    fun `has valid onLeft error if two segments in one lines have different voltage`() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    powerSystemEquivalent110.id to powerSystemEquivalent110,
                    transmissionLineSegment35.id to transmissionLineSegment35,
                    transmissionLineSegment110.id to transmissionLineSegment110,
                    loadForTransmissionLimeSegment110.id to loadForTransmissionLimeSegment110
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent110,
                sourcePort = powerSystemEquivalent110.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[0],
                linkId = bus.ports[0].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                sourceEquipment = transmissionLineSegment35,
                sourcePort = transmissionLineSegment35.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[1],
                linkId = bus.ports[1].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_35
            )
            .connect(
                sourceEquipment = transmissionLineSegment110,
                sourcePort = transmissionLineSegment110.ports[0],
                targetEquipment = transmissionLineSegment35,
                targetPort = transmissionLineSegment35.ports[1],
                linkId = transmissionLineSegment35.ports[1].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                sourceEquipment = loadForTransmissionLimeSegment110,
                sourcePort = loadForTransmissionLimeSegment110.ports[0],
                targetEquipment = transmissionLineSegment110,
                targetPort = transmissionLineSegment110.ports[1],
                linkId = transmissionLineSegment110.ports[1].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .withSubstations(listOf(substation))
            .withTransmissionLines(listOf(transmissionLine))
            .build()

        EitherAssert.assertThat(
            transmissionLineSegmentsValidator.validate(scheme)
        ).containsLeftInstanceOf(
            TransmissionLineValidationError.TransmissionLineHasSegmentWithDifferentVoltages::class.java
        )
    }

    @Test
    fun `has valid onLeft error if two power systems have different voltages`() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    powerSystemEquivalent110.id to powerSystemEquivalent110,
                    powerSystemEquivalent500.id to powerSystemEquivalent500
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent110,
                sourcePort = powerSystemEquivalent110.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[0],
                linkId = bus.ports[0].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                sourceEquipment = powerSystemEquivalent500,
                sourcePort = powerSystemEquivalent500.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[1],
                linkId = bus.ports[1].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_500
            )
            .withSubstations(listOf(substation))
            .build()

        EitherAssert.assertThat(
            voltagesValidator.validate(scheme)
        ).containsLeftInstanceOf(
            VoltageValidationError.ConnectedNodesHaveDifferentVoltageLevels::class.java
        )
    }

    @Test
    fun `has valid onLeft error if synchronous generator and power system have different voltages`() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    powerSystemEquivalent110.id to powerSystemEquivalent110,
                    synchronousGenerator.id to synchronousGenerator
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent110,
                sourcePort = powerSystemEquivalent110.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[0],
                linkId = bus.ports[0].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                sourceEquipment = synchronousGenerator,
                sourcePort = synchronousGenerator.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[1],
                linkId = bus.ports[1].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_500
            )
            .withSubstations(listOf(substation))
            .build()

        EitherAssert.assertThat(
            voltagesValidator.validate(scheme)
        ).containsLeftInstanceOf(
            VoltageValidationError.ConnectedNodesHaveDifferentVoltageLevels::class.java
        )
    }

    @Test
    fun `has valid onLeft error if asynchronous motor and power system have different voltages`() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    powerSystemEquivalent110.id to powerSystemEquivalent110,
                    asynchronousMotor.id to asynchronousMotor
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent110,
                sourcePort = powerSystemEquivalent110.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[0],
                linkId = bus.ports[0].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                sourceEquipment = asynchronousMotor,
                sourcePort = asynchronousMotor.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[1],
                linkId = bus.ports[1].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_500
            )
            .withSubstations(listOf(substation))
            .build()

        EitherAssert.assertThat(
            voltagesValidator.validate(scheme)
        ).containsLeftInstanceOf(
            VoltageValidationError.ConnectedNodesHaveDifferentVoltageLevels::class.java
        )
    }

    @Test
    fun `voltage validate scheme with all types equipment`() {
        val scheme = SchemesForTest
            .buildSchemeWithAllTypesEquipment()

        assertTrue(
            voltagesValidator.validate(scheme).isRight()
        )
        Assertions.assertThatNoException()
    }

    @Test
    fun `has valid onLeft error if three winding power transformer and load have diff voltage`() {
        val scheme = SchemesForTest
            .buildSchemeWithThreeWindingPowerTransformerAndLoadOnLowVoltageSide(
                substation,
                targetEquipmentHighVoltageSide = bus,
                targetPortHighVoltageSide = bus.ports[1],
                linkIdHighVoltageSide = bus.ports[1].links[0],
                targetEquipmentMiddleVoltageSide = loadForThreeWindingPowerTransformer,
                targetPortMiddleVoltageSide = loadForThreeWindingPowerTransformer.ports[0],
                linkIdMiddleVoltageSide = linkIdFirstPortLoad,
                highVoltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110,
                middleVoltageLevelLibId = VoltageLevelLibId.KILOVOLTS_35,
                lowVoltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
            )
            .withNodes(
                mapOf(
                    bus.id to bus,
                    powerSystemEquivalent110.id to powerSystemEquivalent110,
                    loadForThreeWindingPowerTransformer.id to loadForThreeWindingPowerTransformer
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent110,
                sourcePort = powerSystemEquivalent110.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[0],
                linkId = bus.ports[0].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .withSubstations(listOf(substation))
            .build()

        EitherAssert.assertThat(
            voltagesValidator.validate(scheme)
        ).containsLeftInstanceOf(
            VoltageValidationError.ConnectedNodesHaveDifferentVoltageLevels::class.java
        )
    }
}
