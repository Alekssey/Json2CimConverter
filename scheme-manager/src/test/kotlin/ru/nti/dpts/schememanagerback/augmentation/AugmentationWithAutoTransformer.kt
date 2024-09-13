package ru.nti.dpts.schememanagerback.augmentation

import org.assertj.core.api.Assertions
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

class AugmentationWithAutoTransformer {

    private val augmentationService = AugmentationService()

    private val substation = SubstationBuilder().build()

    private val busId = UUID.randomUUID().toString()
    private val powerSystemEquivalentId = UUID.randomUUID().toString()
    private val threeWindingAutoTransformerId = UUID.randomUUID().toString()
    private val loadId = UUID.randomUUID().toString()
    private val asynchronousMotorId = UUID.randomUUID().toString()

    private val linkIdFirstPortBus = UUID.randomUUID().toString()
    private val linkIdSecondPortBus = UUID.randomUUID().toString()
    private val linkIdSecondPortThreeWindingAutoTransformer = UUID.randomUUID().toString()
    private val linkIdThirdPortThreeWindingAutoTransformer = UUID.randomUUID().toString()

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

    private val threeWindingAutoTransformer = EquipmentNodeBuilder()
        .buildThreeWindingPowerTransformer()
        .withId(threeWindingAutoTransformerId)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(threeWindingAutoTransformerId)
                    .withLinks(listOf(linkIdSecondPortBus))
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
                    .withLinks(listOf(linkIdSecondPortThreeWindingAutoTransformer))
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
                    .withLinks(listOf(linkIdThirdPortThreeWindingAutoTransformer))
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
    fun `augment scheme with three winding auto power transformer 110 kV`() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    threeWindingAutoTransformer.id to threeWindingAutoTransformer,
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
                sourceEquipment = threeWindingAutoTransformer,
                sourcePort = threeWindingAutoTransformer.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[1],
                linkId = linkIdSecondPortBus,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = asynchronousMotor,
                sourcePort = asynchronousMotor.ports[0],
                targetEquipment = threeWindingAutoTransformer,
                targetPort = threeWindingAutoTransformer.ports[1],
                linkId = linkIdSecondPortThreeWindingAutoTransformer,
                voltageLevelLibId = null
            )
            .connect(
                sourceEquipment = load,
                sourcePort = load.ports[0],
                targetEquipment = threeWindingAutoTransformer,
                targetPort = threeWindingAutoTransformer.ports[2],
                linkId = linkIdThirdPortThreeWindingAutoTransformer,
                voltageLevelLibId = null
            )
            .withSubstations(listOf(substation))
            .build()

        augmentationService.augment(scheme)

        scheme.nodes.values
            .filter { it.libEquipmentId == EquipmentLibId.THREE_WINDING_POWER_TRANSFORMER }
            .forEach { Assertions.assertThat(it.voltageLevelId).isEqualTo(VoltageLevelLibId.KILOVOLTS_110) }

        scheme.nodes.values
            .filter { it.libEquipmentId == EquipmentLibId.ASYNCHRONOUS_MOTOR }
            .forEach { Assertions.assertThat(it.voltageLevelId).isEqualTo(VoltageLevelLibId.KILOVOLTS_35) }

        scheme.nodes.values
            .filter { it.libEquipmentId == EquipmentLibId.LOAD }
            .forEach { Assertions.assertThat(it.voltageLevelId).isEqualTo(VoltageLevelLibId.KILOVOLTS_10) }
    }
}
