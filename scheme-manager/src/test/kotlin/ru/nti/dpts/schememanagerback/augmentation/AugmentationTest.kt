package ru.nti.dpts.schememanagerback.augmentation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.nti.dpts.schememanagerback.builder.EquipmentNodeBuilder
import ru.nti.dpts.schememanagerback.builder.SchemeBuilder
import ru.nti.dpts.schememanagerback.builder.SubstationBuilder
import ru.nti.dpts.schememanagerback.scheme.service.augmentation.AugmentationService
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import ru.nti.dtps.proto.scheme.equipment.EquipmentNode
import java.util.*

class AugmentationTest {

    private val augmentationService = AugmentationService()

    private val substation = SubstationBuilder().build()

    private val busId = UUID.randomUUID().toString()
    private val powerSystemEquivalentId = UUID.randomUUID().toString()
    private val threeWindingTransformerId = UUID.randomUUID().toString()
    private val loadId = UUID.randomUUID().toString()
    private val asynchronousMotorId = UUID.randomUUID().toString()

    private val linkIdFirstPortBus = UUID.randomUUID().toString()
    private val linkIdSecondPortBus = UUID.randomUUID().toString()
    private val linkIdSecondPortThreeWindingTransformer = UUID.randomUUID().toString()
    private val linkIdThirdPortThreeWindingTransformer = UUID.randomUUID().toString()

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

    private val threeWindingTransformer = EquipmentNodeBuilder()
        .buildThreeWindingPowerTransformer()
        .withId(threeWindingTransformerId)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(threeWindingTransformerId)
                    .withLinks(listOf(linkIdSecondPortBus))
                    .build(),
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.RIGHT)
                    .withLibId(PortLibId.SECOND)
                    .withParentNode(threeWindingTransformerId)
                    .withLinks(listOf(linkIdSecondPortThreeWindingTransformer))
                    .build(),
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                    .withLibId(PortLibId.THIRD)
                    .withParentNode(threeWindingTransformerId)
                    .withLinks(listOf(linkIdThirdPortThreeWindingTransformer))
                    .build()
            )
        )
        .build()

    private val asynchronousMotor = EquipmentNodeBuilder()
        .buildAsynchronousMotor()
        .withId(asynchronousMotorId)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(asynchronousMotorId)
                    .withLinks(listOf(linkIdSecondPortThreeWindingTransformer))
                    .build()
            )
        )
        .build()

    private val load = EquipmentNodeBuilder()
        .buildLoad()
        .withId(loadId)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(loadId)
                    .withLinks(listOf(linkIdThirdPortThreeWindingTransformer))
                    .build()
            )
        )
        .build()

    private val loadTwo = EquipmentNodeBuilder()
        .buildLoad()
        .withId(loadId)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(loadId)
                    .withLinks(listOf(linkIdSecondPortBus))
                    .build()
            )
        )
        .build()

    @Test
    fun `augment scheme without power transformer`() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    powerSystemEquivalent.id to powerSystemEquivalent,
                    loadTwo.id to loadTwo
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
                sourceEquipment = loadTwo,
                sourcePort = loadTwo.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[1],
                linkId = linkIdSecondPortBus,
                voltageLevelLibId = null
            )
            .withSubstations(listOf(substation))
            .build()

        augmentationService.augment(scheme)
        scheme.nodes.values
            .filter { it.libEquipmentId == EquipmentLibId.LOAD }
            .forEach { assertThat(it.voltageLevelId).isEqualTo(VoltageLevelLibId.KILOVOLTS_110) }
    }

    @Test
    fun `augment scheme with three winding power transformer 110 kV`() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    threeWindingTransformer.id to threeWindingTransformer,
                    load.id to load,
                    asynchronousMotor.id to asynchronousMotor,
                    powerSystemEquivalent.id to powerSystemEquivalent
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
                sourceEquipment = threeWindingTransformer,
                sourcePort = threeWindingTransformer.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[1],
                linkId = linkIdSecondPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = asynchronousMotor,
                sourcePort = asynchronousMotor.ports[0],
                targetEquipment = threeWindingTransformer,
                targetPort = threeWindingTransformer.ports[1],
                linkId = linkIdSecondPortThreeWindingTransformer,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = load,
                sourcePort = load.ports[0],
                targetEquipment = threeWindingTransformer,
                targetPort = threeWindingTransformer.ports[2],
                linkId = linkIdThirdPortThreeWindingTransformer,
                voltageLevelLibId = null
            )
            .withSubstations(listOf(substation))
            .build()

        augmentationService.augment(scheme)

        scheme.nodes.values
            .filter { it.libEquipmentId == EquipmentLibId.THREE_WINDING_POWER_TRANSFORMER }
            .forEach { assertThat(it.voltageLevelId).isEqualTo(VoltageLevelLibId.KILOVOLTS_110) }

        scheme.nodes.values
            .filter { it.libEquipmentId == EquipmentLibId.ASYNCHRONOUS_MOTOR }
            .forEach { assertThat(it.voltageLevelId).isEqualTo(VoltageLevelLibId.KILOVOLTS_35) }

        scheme.nodes.values
            .filter { it.libEquipmentId == EquipmentLibId.LOAD }
            .forEach { assertThat(it.voltageLevelId).isEqualTo(VoltageLevelLibId.KILOVOLTS_10) }
    }
}
