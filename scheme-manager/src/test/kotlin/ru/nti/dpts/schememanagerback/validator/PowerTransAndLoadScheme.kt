package ru.nti.dpts.schememanagerback.validator

import `in`.rcard.assertj.arrowcore.EitherAssert
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.nti.dpts.schememanagerback.builder.EquipmentNodeBuilder
import ru.nti.dpts.schememanagerback.builder.SchemeBuilder
import ru.nti.dpts.schememanagerback.builder.SubstationBuilder
import ru.nti.dpts.schememanagerback.scheme.service.augmentation.AugmentationService
import ru.nti.dpts.schememanagerback.scheme.service.validator.ValidatorService
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.SchemeContainsIslandsError
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.proto.lib.control.ControlLibId
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.field.FieldLibId
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import ru.nti.dtps.proto.scheme.equipment.EquipmentNode
import java.util.*

@SpringBootTest(classes = [TestConfigurationToValidation::class])
class PowerTransAndLoadScheme {

    @Autowired
    lateinit var validatorService: ValidatorService

    @Autowired
    lateinit var augmentationService: AugmentationService

    private val substation = SubstationBuilder().build()

    private val busWithOnePortId = UUID.randomUUID().toString()
    private val busWithTwoPortId = UUID.randomUUID().toString()
    private val busWithThreePortId = UUID.randomUUID().toString()

    private val powerSystemEquivalentId = UUID.randomUUID().toString()
    private val twoWindingTransformerFirstId = UUID.randomUUID().toString()
    private val twoWindingTransformerSecondId = UUID.randomUUID().toString()
    private val loadFirstId = UUID.randomUUID().toString()
    private val loadSecondId = UUID.randomUUID().toString()
    private val loadThirdId = UUID.randomUUID().toString()
    private val shortCircuitId = UUID.randomUUID().toString()

    private val linkIdFirstPortBus = UUID.randomUUID().toString()
    private val linkIdSecondPortBus = UUID.randomUUID().toString()
    private val linkIdThirdPortBus = UUID.randomUUID().toString()
    private val linkIdSecondPortTwoWindingTransformerFirst = UUID.randomUUID().toString()
    private val linkIdSecondPortTwoWindingTransformerSecond = UUID.randomUUID().toString()

    private val busWithOnePorts = EquipmentNodeBuilder().buildBus()
        .withId(busWithOnePortId)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                    .withLibId(PortLibId.NO_ID)
                    .withParentNode(busWithOnePortId)
                    .withLinks(listOf(linkIdFirstPortBus))
                    .build()
            )
        )
        .build()

    private val busWithTwoPorts = EquipmentNodeBuilder().buildBus()
        .withId(busWithTwoPortId)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                    .withLibId(PortLibId.NO_ID)
                    .withParentNode(busWithTwoPortId)
                    .withLinks(listOf(linkIdFirstPortBus))
                    .build(),
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                    .withLibId(PortLibId.NO_ID)
                    .withParentNode(busWithTwoPortId)
                    .withLinks(listOf(linkIdSecondPortBus))
                    .build()
            )
        )
        .build()

    private val busWithThreePorts = EquipmentNodeBuilder().buildBus()
        .withId(busWithThreePortId)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                    .withLibId(PortLibId.NO_ID)
                    .withParentNode(busWithThreePortId)
                    .withLinks(listOf(linkIdFirstPortBus))
                    .build(),
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                    .withLibId(PortLibId.NO_ID)
                    .withParentNode(busWithThreePortId)
                    .withLinks(listOf(linkIdSecondPortBus))
                    .build(),
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                    .withLibId(PortLibId.NO_ID)
                    .withParentNode(busWithThreePortId)
                    .withLinks(listOf(linkIdThirdPortBus))
                    .build()
            )
        )
        .build()

    private val powerSystemEquivalent = EquipmentNodeBuilder()
        .buildPowerSystemEquivalent()
        .withId(powerSystemEquivalentId)
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

    private val twoWindingTransformerFirst = EquipmentNodeBuilder()
        .buildTwoWindingPowerTransformer()
        .withId(twoWindingTransformerFirstId)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(twoWindingTransformerFirstId)
                    .withLinks(listOf(linkIdSecondPortBus))
                    .build(),
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.RIGHT)
                    .withLibId(PortLibId.SECOND)
                    .withParentNode(twoWindingTransformerFirstId)
                    .withLinks(listOf(linkIdSecondPortTwoWindingTransformerFirst))
                    .build()
            )
        )
        .withFields(mapOf(FieldLibId.SECOND_WINDING_RATED_VOLTAGE to "120"))
        .build()

    private val twoWindingTransformerSecond = EquipmentNodeBuilder()
        .buildTwoWindingPowerTransformer()
        .withId(twoWindingTransformerSecondId)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(twoWindingTransformerSecondId)
                    .withLinks(listOf(linkIdThirdPortBus))
                    .build(),
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.RIGHT)
                    .withLibId(PortLibId.SECOND)
                    .withParentNode(twoWindingTransformerSecondId)
                    .withLinks(listOf(linkIdSecondPortTwoWindingTransformerSecond))
                    .build()
            )
        )
        .withFields(mapOf(FieldLibId.SECOND_WINDING_RATED_VOLTAGE to "120"))
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
                    .withLinks(listOf(linkIdSecondPortTwoWindingTransformerFirst))
                    .build()
            )
        )
        .withFields(mapOf(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to "110"))
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
                    .withLinks(listOf(linkIdSecondPortTwoWindingTransformerSecond))
                    .build()
            )
        )
        .withFields(mapOf(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to "110"))
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
                    .withLinks(listOf(linkIdFirstPortBus))
                    .build()
            )
        )
        .withFields(mapOf(FieldLibId.RATED_VOLTAGE_LINE_TO_LINE to "110"))
        .build()

    private val shortCircuit = EquipmentNodeBuilder()
        .buildShortCircuit()
        .withId(shortCircuitId)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(shortCircuitId)
                    .withLinks(listOf(linkIdThirdPortBus))
                    .build()
            )
        )
        .build()

    @Test
    fun `assert scheme is valid`() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    busWithTwoPorts.id to busWithTwoPorts,
                    powerSystemEquivalent.id to powerSystemEquivalent,
                    twoWindingTransformerFirst.id to twoWindingTransformerFirst,
                    loadFirst.id to loadFirst
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent,
                sourcePort = powerSystemEquivalent.ports[0],
                targetEquipment = busWithTwoPorts,
                targetPort = busWithTwoPorts.ports[0],
                linkId = linkIdFirstPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = twoWindingTransformerFirst,
                sourcePort = twoWindingTransformerFirst.ports[0],
                targetEquipment = busWithTwoPorts,
                targetPort = busWithTwoPorts.ports[1],
                linkId = linkIdSecondPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = loadFirst,
                sourcePort = loadFirst.ports[0],
                targetEquipment = twoWindingTransformerFirst,
                targetPort = twoWindingTransformerFirst.ports[1],
                linkId = linkIdSecondPortTwoWindingTransformerFirst,
                voltageLevelLibId = null
            )
            .withSubstations(listOf(substation))
            .build()

        assertThat(scheme.nodes.values).allMatch { node ->
            node.libEquipmentId == EquipmentLibId.TWO_WINDING_POWER_TRANSFORMER || node.voltageLevelId == null
        }

        assertTrue(
            validatorService.validate(scheme).isRight()
        )
        augmentationService.augment(scheme)

        assertThat(scheme.nodes.values).allMatch { node ->
            node.voltageLevelId == VoltageLevelLibId.KILOVOLTS_110
        }
        assertThat(scheme.links.values).allMatch { link ->
            link.voltageLevelId == VoltageLevelLibId.KILOVOLTS_110
        }
    }

    @Test
    fun power2TransCorrectVoltagesValidate() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    busWithThreePorts.id to busWithThreePorts,
                    powerSystemEquivalent.id to powerSystemEquivalent,
                    twoWindingTransformerFirst.id to twoWindingTransformerFirst,
                    twoWindingTransformerSecond.id to twoWindingTransformerSecond,
                    loadFirst.id to loadFirst,
                    loadSecond.id to loadSecond
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent,
                sourcePort = powerSystemEquivalent.ports[0],
                targetEquipment = busWithThreePorts,
                targetPort = busWithThreePorts.ports[0],
                linkId = linkIdFirstPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = twoWindingTransformerFirst,
                sourcePort = twoWindingTransformerFirst.ports[0],
                targetEquipment = busWithThreePorts,
                targetPort = busWithThreePorts.ports[1],
                linkId = linkIdSecondPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = twoWindingTransformerSecond,
                sourcePort = twoWindingTransformerSecond.ports[0],
                targetEquipment = busWithThreePorts,
                targetPort = busWithThreePorts.ports[2],
                linkId = linkIdThirdPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = loadFirst,
                sourcePort = loadFirst.ports[0],
                targetEquipment = twoWindingTransformerFirst,
                targetPort = twoWindingTransformerFirst.ports[1],
                linkId = linkIdSecondPortTwoWindingTransformerFirst,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = loadSecond,
                sourcePort = loadSecond.ports[0],
                targetEquipment = twoWindingTransformerSecond,
                targetPort = twoWindingTransformerSecond.ports[1],
                linkId = linkIdSecondPortTwoWindingTransformerSecond,
                voltageLevelLibId = null
            )
            .withSubstations(listOf(substation))
            .build()

        assertTrue(
            validatorService.validate(scheme).isRight()
        )
        Assertions.assertThatNoException()
    }

    @Test
    fun powerTransAndLoadWithIsland() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    busWithTwoPorts.id to busWithTwoPorts,
                    powerSystemEquivalent.id to powerSystemEquivalent,
                    twoWindingTransformerFirst.id to twoWindingTransformerFirst,
                    loadFirst.id to loadFirst,
                    busWithOnePorts.id to busWithOnePorts,
                    loadThird.id to loadThird
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent,
                sourcePort = powerSystemEquivalent.ports[0],
                targetEquipment = busWithTwoPorts,
                targetPort = busWithTwoPorts.ports[0],
                linkId = linkIdFirstPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = twoWindingTransformerFirst,
                sourcePort = twoWindingTransformerFirst.ports[0],
                targetEquipment = busWithTwoPorts,
                targetPort = busWithTwoPorts.ports[1],
                linkId = linkIdSecondPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = loadFirst,
                sourcePort = loadFirst.ports[0],
                targetEquipment = twoWindingTransformerFirst,
                targetPort = twoWindingTransformerFirst.ports[1],
                linkId = linkIdSecondPortTwoWindingTransformerFirst,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = loadThird,
                sourcePort = loadThird.ports[0],
                targetEquipment = busWithOnePorts,
                targetPort = busWithOnePorts.ports[0],
                linkId = linkIdFirstPortBus,
                voltageLevelLibId = null
            )
            .withSubstations(listOf(substation))
            .build()

        EitherAssert.assertThat(
            validatorService.validate(scheme)
        ).containsLeftInstanceOf(SchemeContainsIslandsError::class.java)
    }

    @Test
    fun allControlsCreated() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    busWithThreePorts.id to busWithThreePorts,
                    powerSystemEquivalent.id to powerSystemEquivalent,
                    twoWindingTransformerFirst.id to twoWindingTransformerFirst,
                    loadFirst.id to loadFirst,
                    shortCircuit.id to shortCircuit
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent,
                sourcePort = powerSystemEquivalent.ports[0],
                targetEquipment = busWithThreePorts,
                targetPort = busWithThreePorts.ports[0],
                linkId = linkIdFirstPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = twoWindingTransformerFirst,
                sourcePort = twoWindingTransformerFirst.ports[0],
                targetEquipment = busWithThreePorts,
                targetPort = busWithThreePorts.ports[1],
                linkId = linkIdSecondPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = loadFirst,
                sourcePort = loadFirst.ports[0],
                targetEquipment = twoWindingTransformerFirst,
                targetPort = twoWindingTransformerFirst.ports[1],
                linkId = linkIdSecondPortTwoWindingTransformerFirst,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = shortCircuit,
                sourcePort = shortCircuit.ports[0],
                targetEquipment = busWithThreePorts,
                targetPort = busWithThreePorts.ports[2],
                linkId = linkIdThirdPortBus,
                voltageLevelLibId = null
            )
            .withSubstations(listOf(substation))
            .build()

        validatorService.validate(scheme)
        augmentationService.augment(scheme)

        scheme.nodes.values.forEach { equipmentNode ->
            val controls = EquipmentMetaInfoManager.getEquipmentLibById(equipmentNode.libEquipmentId).controls
            controls
                .filter { control -> control.id != ControlLibId.TAP_CHANGER_POSITION }
                .forEach { control ->
                    assertThat(equipmentNode.controls[control.id]).isNotNull
                }
            equipmentNode.controls.keys.forEach { controlId ->
                assertThat(controls.any { it.id == controlId }).isTrue()
            }
        }
    }
}
