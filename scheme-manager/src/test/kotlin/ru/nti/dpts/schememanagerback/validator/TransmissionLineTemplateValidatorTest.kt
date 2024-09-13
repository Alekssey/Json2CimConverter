package ru.nti.dpts.schememanagerback.validator

import `in`.rcard.assertj.arrowcore.EitherAssert
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.nti.dpts.schememanagerback.builder.EquipmentNodeBuilder
import ru.nti.dpts.schememanagerback.builder.SchemeBuilder
import ru.nti.dpts.schememanagerback.builder.SubstationBuilder
import ru.nti.dpts.schememanagerback.builder.TransmissionLineBuilder
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.TransmissionLineSegmentsValidator
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.TransmissionLineValidationError
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import ru.nti.dtps.proto.scheme.equipment.EquipmentNode
import java.util.*

@SpringBootTest(classes = [TestConfigurationToValidation::class])
class TransmissionLineTemplateValidatorTest(
    @Autowired
    val transmissionLineSegmentsValidator: TransmissionLineSegmentsValidator
) {

    private val substation = SubstationBuilder().build()
    private val transmissionLineFirst = TransmissionLineBuilder().build()
    private val transmissionLineSecond = TransmissionLineBuilder().build()

    private val busId = UUID.randomUUID().toString()
    private val linkIdFirstPortBus = UUID.randomUUID().toString()
    private val linkIdSecondPortBus = UUID.randomUUID().toString()
    private val linkIdThirdPortBus = UUID.randomUUID().toString()
    private val powerSystemEquivalentId = UUID.randomUUID().toString()
    private val loadFirstId = UUID.randomUUID().toString()
    private val loadSecondId = UUID.randomUUID().toString()
    private val loadThirdId = UUID.randomUUID().toString()
    private val lineSegmentFirstId = UUID.randomUUID().toString()
    private val lineSegmentSecondId = UUID.randomUUID().toString()
    private val lineSegmentThirdId = UUID.randomUUID().toString()
    private val lineSegmentFourthId = UUID.randomUUID().toString()

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

    private val powerSystemEquivalent = EquipmentNodeBuilder()
        .buildPowerSystemEquivalent()
        .withId(powerSystemEquivalentId)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(powerSystemEquivalentId)
                    .withLinks(listOf(linkIdFirstPortBus))
                    .build()
            )
        )
        .build()

    private val linkIdSecondPortLineSegmentFirst = UUID.randomUUID().toString()
    private val transmissionLineSegmentFirst = EquipmentNodeBuilder()
        .buildTransmissionLineSegment()
        .withId(lineSegmentFirstId)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withTransmissionLineId(transmissionLineFirst.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(lineSegmentFirstId)
                    .withLinks(listOf(linkIdFirstPortBus))
                    .build(),
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                    .withLibId(PortLibId.SECOND)
                    .withParentNode(lineSegmentFirstId)
                    .withLinks(listOf(linkIdSecondPortLineSegmentFirst))
                    .build()
            )
        )
        .withFields(mapOf(FieldLibId.VOLTAGE_LEVEL to "KILOVOLTS_110"))
        .build()

    private val linkIdSecondPortLineSegmentSecond = UUID.randomUUID().toString()
    private val transmissionLineSegmentSecond = EquipmentNodeBuilder()
        .buildTransmissionLineSegment()
        .withId(lineSegmentSecondId)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withTransmissionLineId(transmissionLineFirst.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(lineSegmentSecondId)
                    .withLinks(listOf(linkIdSecondPortLineSegmentFirst))
                    .build(),
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                    .withLibId(PortLibId.SECOND)
                    .withParentNode(lineSegmentSecondId)
                    .withLinks(listOf(linkIdSecondPortLineSegmentSecond))
                    .build()
            )
        )
        .withFields(mapOf(FieldLibId.VOLTAGE_LEVEL to "KILOVOLTS_110"))
        .build()

    private val loadFirst = EquipmentNodeBuilder()
        .buildLoad()
        .withId(loadFirstId)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(loadFirstId)
                    .withLinks(listOf(linkIdSecondPortLineSegmentSecond))
                    .build()
            )
        )
        .build()

    private val linkIdSecondPortLineSegmentThird = UUID.randomUUID().toString()
    private val transmissionLineSegmentThird = EquipmentNodeBuilder()
        .buildTransmissionLineSegment()
        .withId(lineSegmentThirdId)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withTransmissionLineId(transmissionLineSecond.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(lineSegmentThirdId)
                    .withLinks(listOf(linkIdThirdPortBus))
                    .build(),
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                    .withLibId(PortLibId.SECOND)
                    .withParentNode(lineSegmentThirdId)
                    .withLinks(listOf(linkIdSecondPortLineSegmentThird))
                    .build()
            )
        )
        .withFields(mapOf(FieldLibId.VOLTAGE_LEVEL to "KILOVOLTS_110"))
        .build()

    private val loadSecond = EquipmentNodeBuilder()
        .buildLoad()
        .withId(loadSecondId)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(loadSecondId)
                    .withLinks(listOf(linkIdSecondPortLineSegmentThird))
                    .build()
            )
        )
        .build()

    private val linkIdSecondPortLineSegmentFourth = UUID.randomUUID().toString()
    private val transmissionLineSegmentFourth = EquipmentNodeBuilder()
        .buildTransmissionLineSegment()
        .withId(lineSegmentFourthId)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
        .withTransmissionLineId(transmissionLineFirst.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(lineSegmentFourthId)
                    .withLinks(listOf(linkIdSecondPortLineSegmentSecond))
                    .build(),
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                    .withLibId(PortLibId.SECOND)
                    .withParentNode(lineSegmentFourthId)
                    .withLinks(listOf(linkIdSecondPortLineSegmentFourth))
                    .build()
            )
        )
        .withFields(mapOf(FieldLibId.VOLTAGE_LEVEL to "KILOVOLTS_10"))
        .build()

    private val loadThird = EquipmentNodeBuilder()
        .buildLoad()
        .withId(loadThirdId)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(loadThirdId)
                    .withLinks(listOf(linkIdSecondPortLineSegmentFourth))
                    .build()
            )
        )
        .build()

    @Test
    fun `scheme with t lines is valid`() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    powerSystemEquivalent.id to powerSystemEquivalent,
                    transmissionLineSegmentFirst.id to transmissionLineSegmentFirst,
                    transmissionLineSegmentSecond.id to transmissionLineSegmentSecond,
                    transmissionLineSegmentThird.id to transmissionLineSegmentThird,
                    loadFirst.id to loadFirst,
                    loadSecond.id to loadSecond
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent,
                sourcePort = powerSystemEquivalent.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[0],
                linkId = linkIdFirstPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegmentFirst,
                sourcePort = transmissionLineSegmentFirst.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[1],
                linkId = linkIdSecondPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegmentThird,
                sourcePort = transmissionLineSegmentThird.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[2],
                linkId = linkIdThirdPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegmentSecond,
                sourcePort = transmissionLineSegmentSecond.ports[0],
                targetEquipment = transmissionLineSegmentFirst,
                targetPort = transmissionLineSegmentFirst.ports[1],
                linkId = linkIdSecondPortLineSegmentFirst,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = loadFirst,
                sourcePort = loadFirst.ports[0],
                targetEquipment = transmissionLineSegmentSecond,
                targetPort = transmissionLineSegmentSecond.ports[1],
                linkId = linkIdSecondPortLineSegmentSecond,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = loadSecond,
                sourcePort = loadSecond.ports[0],
                targetEquipment = transmissionLineSegmentThird,
                targetPort = transmissionLineSegmentThird.ports[1],
                linkId = linkIdSecondPortLineSegmentThird,
                voltageLevelLibId = null
            )
            .withSubstations(listOf(substation))
            .withTransmissionLines(listOf(transmissionLineFirst, transmissionLineSecond))
            .build()

        assertTrue(transmissionLineSegmentsValidator.validate(scheme).isRight())
    }

    @Test
    fun `has valid onLeft error if voltage level are different`() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    powerSystemEquivalent.id to powerSystemEquivalent,
                    transmissionLineSegmentFirst.id to transmissionLineSegmentFirst,
                    transmissionLineSegmentSecond.id to transmissionLineSegmentSecond,
                    transmissionLineSegmentFourth.id to transmissionLineSegmentFourth,
                    loadThird.id to loadThird
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent,
                sourcePort = powerSystemEquivalent.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[0],
                linkId = linkIdFirstPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegmentFirst,
                sourcePort = transmissionLineSegmentFirst.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[1],
                linkId = linkIdSecondPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegmentSecond,
                sourcePort = transmissionLineSegmentSecond.ports[0],
                targetEquipment = transmissionLineSegmentFirst,
                targetPort = transmissionLineSegmentFirst.ports[1],
                linkId = linkIdSecondPortLineSegmentFirst,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegmentFourth,
                sourcePort = transmissionLineSegmentFourth.ports[0],
                targetEquipment = transmissionLineSegmentSecond,
                targetPort = transmissionLineSegmentSecond.ports[1],
                linkId = linkIdSecondPortLineSegmentSecond,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = loadThird,
                sourcePort = loadThird.ports[0],
                targetEquipment = transmissionLineSegmentFourth,
                targetPort = transmissionLineSegmentFourth.ports[1],
                linkId = linkIdSecondPortLineSegmentFourth,
                voltageLevelLibId = null
            )
            .withSubstations(listOf(substation))
            .withTransmissionLines(listOf(transmissionLineFirst))
            .build()

        EitherAssert.assertThat(
            transmissionLineSegmentsValidator.validate(scheme)
        ).containsLeftInstanceOf(TransmissionLineValidationError.TransmissionLineHasSegmentWithDifferentVoltages::class.java)
    }

    @Test
    fun `validation right if connectivity node locate between line segments`() {
        val linkIdSecondPortLineSegmentThird = UUID.randomUUID().toString()
        val transmissionLineSegmentThird = EquipmentNodeBuilder()
            .buildTransmissionLineSegment()
            .withId(lineSegmentThirdId)
            .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
            .withTransmissionLineId(transmissionLineFirst.id)
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(lineSegmentThirdId)
                        .withLinks(listOf(linkIdSecondPortLineSegmentSecond))
                        .build(),
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                        .withLibId(PortLibId.SECOND)
                        .withParentNode(lineSegmentThirdId)
                        .withLinks(listOf(linkIdSecondPortLineSegmentThird))
                        .build()
                )
            )
            .withFields(mapOf(FieldLibId.VOLTAGE_LEVEL to "KILOVOLTS_110"))
            .build()

        val loadSecond = EquipmentNodeBuilder()
            .buildLoad()
            .withId(loadSecondId)
            .withSubstationId(substation.id)
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(loadSecondId)
                        .withLinks(listOf(linkIdSecondPortLineSegmentThird))
                        .build()
                )
            )
            .build()

        val linkIdLoad = UUID.randomUUID().toString()
        val loadThird = EquipmentNodeBuilder()
            .buildLoad()
            .withId(loadThirdId)
            .withSubstationId(substation.id)
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(loadThirdId)
                        .withLinks(listOf(linkIdLoad))
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
                        .withLinks(listOf(linkIdSecondPortLineSegmentSecond, linkIdLoad, linkIdSecondPortLineSegmentThird))
                        .build()
                )
            )
            .build()

        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    powerSystemEquivalent.id to powerSystemEquivalent,
                    transmissionLineSegmentFirst.id to transmissionLineSegmentFirst,
                    transmissionLineSegmentSecond.id to transmissionLineSegmentSecond,
                    transmissionLineSegmentThird.id to transmissionLineSegmentThird,
                    loadSecond.id to loadSecond,
                    loadThird.id to loadThird,
                    connectivity.id to connectivity
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent,
                sourcePort = powerSystemEquivalent.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[0],
                linkId = linkIdFirstPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegmentFirst,
                sourcePort = transmissionLineSegmentFirst.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[1],
                linkId = linkIdSecondPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegmentSecond,
                sourcePort = transmissionLineSegmentSecond.ports[0],
                targetEquipment = transmissionLineSegmentFirst,
                targetPort = transmissionLineSegmentFirst.ports[1],
                linkId = linkIdSecondPortLineSegmentFirst,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegmentSecond,
                sourcePort = transmissionLineSegmentSecond.ports[1],
                targetEquipment = connectivity,
                targetPort = connectivity.ports[0],
                linkId = linkIdSecondPortLineSegmentSecond,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = loadThird,
                sourcePort = loadThird.ports[0],
                targetEquipment = connectivity,
                targetPort = connectivity.ports[0],
                linkId = linkIdLoad,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegmentThird,
                sourcePort = transmissionLineSegmentThird.ports[0],
                targetEquipment = connectivity,
                targetPort = connectivity.ports[0],
                linkId = transmissionLineSegmentThird.ports[0].links[0],
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = loadSecond,
                sourcePort = loadSecond.ports[0],
                targetEquipment = transmissionLineSegmentThird,
                targetPort = transmissionLineSegmentThird.ports[1],
                linkId = transmissionLineSegmentThird.ports[1].links[0],
                voltageLevelLibId = null
            )
            .withSubstations(listOf(substation))
            .withTransmissionLines(listOf(transmissionLineFirst))
            .build()

        assertTrue(
            transmissionLineSegmentsValidator.validate(scheme).isRight()
        )
        Assertions.assertThatNoException()
    }

    @Test
    fun `validation right  if connectivity node locate between three line segments`() {
        val linkIdFirstPortLineSegmentThird = UUID.randomUUID().toString()
        val linkIdSecondPortLineSegmentThird = UUID.randomUUID().toString()
        val transmissionLineSegmentThird = EquipmentNodeBuilder()
            .buildTransmissionLineSegment()
            .withId(lineSegmentThirdId)
            .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
            .withTransmissionLineId(transmissionLineFirst.id)
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(lineSegmentThirdId)
                        .withLinks(listOf(linkIdFirstPortLineSegmentThird))
                        .build(),
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                        .withLibId(PortLibId.SECOND)
                        .withParentNode(lineSegmentThirdId)
                        .withLinks(listOf(linkIdSecondPortLineSegmentThird))
                        .build()
                )
            )
            .withFields(mapOf(FieldLibId.VOLTAGE_LEVEL to "KILOVOLTS_110"))
            .build()

        val linkIdFirstPortLineSegmentFourth = UUID.randomUUID().toString()
        val linkIdSecondPortLineSegmentFourth = UUID.randomUUID().toString()
        val transmissionLineSegmentFourth = EquipmentNodeBuilder()
            .buildTransmissionLineSegment()
            .withId(lineSegmentFourthId)
            .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
            .withTransmissionLineId(transmissionLineFirst.id)
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(lineSegmentFourthId)
                        .withLinks(listOf(linkIdFirstPortLineSegmentFourth))
                        .build(),
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                        .withLibId(PortLibId.SECOND)
                        .withParentNode(lineSegmentFourthId)
                        .withLinks(listOf(linkIdSecondPortLineSegmentFourth))
                        .build()
                )
            )
            .withFields(mapOf(FieldLibId.VOLTAGE_LEVEL to "KILOVOLTS_110"))
            .build()

        val loadSecond = EquipmentNodeBuilder()
            .buildLoad()
            .withId(loadSecondId)
            .withSubstationId(substation.id)
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(loadSecondId)
                        .withLinks(listOf(linkIdSecondPortLineSegmentThird))
                        .build()
                )
            )
            .build()

        val loadThird = EquipmentNodeBuilder()
            .buildLoad()
            .withId(loadThirdId)
            .withSubstationId(substation.id)
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(loadThirdId)
                        .withLinks(listOf(linkIdSecondPortLineSegmentFourth))
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
                        .withLinks(listOf(linkIdSecondPortLineSegmentSecond, linkIdFirstPortLineSegmentFourth, linkIdFirstPortLineSegmentThird))
                        .build()
                )
            )
            .build()

        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    powerSystemEquivalent.id to powerSystemEquivalent,
                    transmissionLineSegmentFirst.id to transmissionLineSegmentFirst,
                    transmissionLineSegmentSecond.id to transmissionLineSegmentSecond,
                    transmissionLineSegmentThird.id to transmissionLineSegmentThird,
                    transmissionLineSegmentFourth.id to transmissionLineSegmentFourth,
                    loadSecond.id to loadSecond,
                    loadThird.id to loadThird,
                    connectivity.id to connectivity
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent,
                sourcePort = powerSystemEquivalent.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[0],
                linkId = linkIdFirstPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegmentFirst,
                sourcePort = transmissionLineSegmentFirst.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[1],
                linkId = linkIdSecondPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegmentSecond,
                sourcePort = transmissionLineSegmentSecond.ports[0],
                targetEquipment = transmissionLineSegmentFirst,
                targetPort = transmissionLineSegmentFirst.ports[1],
                linkId = linkIdSecondPortLineSegmentFirst,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegmentSecond,
                sourcePort = transmissionLineSegmentSecond.ports[1],
                targetEquipment = connectivity,
                targetPort = connectivity.ports[0],
                linkId = linkIdSecondPortLineSegmentSecond,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegmentFourth,
                sourcePort = transmissionLineSegmentFourth.ports[0],
                targetEquipment = connectivity,
                targetPort = connectivity.ports[0],
                linkId = linkIdFirstPortLineSegmentFourth,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegmentThird,
                sourcePort = transmissionLineSegmentThird.ports[0],
                targetEquipment = connectivity,
                targetPort = connectivity.ports[0],
                linkId = linkIdFirstPortLineSegmentThird,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = loadThird,
                sourcePort = loadThird.ports[0],
                targetEquipment = transmissionLineSegmentFourth,
                targetPort = transmissionLineSegmentFourth.ports[1],
                linkId = linkIdSecondPortLineSegmentFourth,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = loadSecond,
                sourcePort = loadSecond.ports[0],
                targetEquipment = transmissionLineSegmentThird,
                targetPort = transmissionLineSegmentThird.ports[1],
                linkId = linkIdSecondPortLineSegmentThird,
                voltageLevelLibId = null
            )
            .withSubstations(listOf(substation))
            .withTransmissionLines(listOf(transmissionLineFirst))
            .build()

        assertTrue(
            transmissionLineSegmentsValidator.validate(scheme).isRight()
        )
        Assertions.assertThatNoException()
    }

    @Test
    fun `has valid onLeft error if circuit breaker node locate between line segments`() {
        val circuitBreakerId = UUID.randomUUID().toString()
        val linkIdSecondPortCircuitBreaker = UUID.randomUUID().toString()
        val circuitBreaker = EquipmentNodeBuilder()
            .buildCircuitBreaker()
            .withId(circuitBreakerId)
            .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
            .withSubstationId(substation.id)
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(circuitBreakerId)
                        .withLinks(listOf(linkIdSecondPortLineSegmentSecond))
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

        val linkIdSecondPortLineSegmentThird = UUID.randomUUID().toString()
        val transmissionLineSegmentThird = EquipmentNodeBuilder()
            .buildTransmissionLineSegment()
            .withId(lineSegmentThirdId)
            .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
            .withTransmissionLineId(transmissionLineFirst.id)
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(lineSegmentThirdId)
                        .withLinks(listOf(linkIdSecondPortCircuitBreaker))
                        .build(),
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                        .withLibId(PortLibId.SECOND)
                        .withParentNode(lineSegmentThirdId)
                        .withLinks(listOf(linkIdSecondPortLineSegmentThird))
                        .build()
                )
            )
            .withFields(mapOf(FieldLibId.VOLTAGE_LEVEL to "KILOVOLTS_110"))
            .build()

        val loadSecond = EquipmentNodeBuilder()
            .buildLoad()
            .withId(loadSecondId)
            .withSubstationId(substation.id)
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(loadSecondId)
                        .withLinks(listOf(linkIdSecondPortLineSegmentThird))
                        .build()
                )
            )
            .build()

        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    powerSystemEquivalent.id to powerSystemEquivalent,
                    transmissionLineSegmentFirst.id to transmissionLineSegmentFirst,
                    transmissionLineSegmentSecond.id to transmissionLineSegmentSecond,
                    transmissionLineSegmentThird.id to transmissionLineSegmentThird,
                    loadSecond.id to loadSecond,
                    circuitBreaker.id to circuitBreaker
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent,
                sourcePort = powerSystemEquivalent.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[0],
                linkId = linkIdFirstPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegmentFirst,
                sourcePort = transmissionLineSegmentFirst.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[1],
                linkId = linkIdSecondPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegmentSecond,
                sourcePort = transmissionLineSegmentSecond.ports[0],
                targetEquipment = transmissionLineSegmentFirst,
                targetPort = transmissionLineSegmentFirst.ports[1],
                linkId = linkIdSecondPortLineSegmentFirst,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = circuitBreaker,
                sourcePort = circuitBreaker.ports[0],
                targetEquipment = transmissionLineSegmentSecond,
                targetPort = transmissionLineSegmentSecond.ports[1],
                linkId = linkIdSecondPortLineSegmentSecond,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = transmissionLineSegmentThird,
                sourcePort = transmissionLineSegmentThird.ports[0],
                targetEquipment = circuitBreaker,
                targetPort = circuitBreaker.ports[1],
                linkId = linkIdSecondPortCircuitBreaker,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = loadSecond,
                sourcePort = loadSecond.ports[0],
                targetEquipment = transmissionLineSegmentThird,
                targetPort = transmissionLineSegmentThird.ports[1],
                linkId = linkIdSecondPortLineSegmentThird,
                voltageLevelLibId = null
            )
            .withSubstations(listOf(substation))
            .withTransmissionLines(listOf(transmissionLineFirst))
            .build()

        EitherAssert.assertThat(
            transmissionLineSegmentsValidator.validate(scheme)
        ).containsLeftInstanceOf(
            TransmissionLineValidationError.TransmissionLineSegmentsDoesNotConnectedError::class.java
        )
    }
}
