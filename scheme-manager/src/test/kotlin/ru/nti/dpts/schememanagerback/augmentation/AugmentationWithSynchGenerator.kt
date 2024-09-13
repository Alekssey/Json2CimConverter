package ru.nti.dpts.schememanagerback.augmentation

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import ru.nti.dpts.schememanagerback.builder.EquipmentNodeBuilder
import ru.nti.dpts.schememanagerback.builder.SchemeBuilder
import ru.nti.dpts.schememanagerback.builder.SubstationBuilder
import ru.nti.dpts.schememanagerback.scheme.service.augmentation.AugmentationService
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import ru.nti.dtps.proto.scheme.equipment.EquipmentNode
import java.util.*

class AugmentationWithSynchGenerator {

    private val augmentationService = AugmentationService()

    private val substation = SubstationBuilder().build()

    private val busId = UUID.randomUUID().toString()
    private val synchGeneratorId = UUID.randomUUID().toString()
    private val loadId = UUID.randomUUID().toString()

    private val linkIdFirstPortBus = UUID.randomUUID().toString()
    private val linkIdSecondPortBus = UUID.randomUUID().toString()

    private val bus = EquipmentNodeBuilder().buildBus()
        .withId(busId)
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

    private val synchGenerator = EquipmentNodeBuilder()
        .buildSynchronousGenerator()
        .withId(synchGeneratorId)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(synchGeneratorId)
                    .withLinks(listOf(linkIdFirstPortBus))
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
                    .withLinks(listOf(linkIdSecondPortBus))
                    .build()
            )
        )
        .build()

    @Test
    fun `augment with synch generator`() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    synchGenerator.id to synchGenerator,
                    load.id to load
                )
            )
            .connect(
                linkId = linkIdFirstPortBus,
                sourceEquipment = synchGenerator,
                targetEquipment = bus,
                sourcePort = synchGenerator.ports[0],
                targetPort = bus.ports[0],
                voltageLevelLibId = null
            )
            .connect(
                linkId = linkIdSecondPortBus,
                sourceEquipment = load,
                targetEquipment = bus,
                sourcePort = load.ports[0],
                targetPort = bus.ports[1],
                voltageLevelLibId = null
            )
            .withSubstations(listOf(substation))
            .build()

        augmentationService.augment(scheme)
        scheme.nodes.values
            .forEach { Assertions.assertThat(it.voltageLevelId).isEqualTo(VoltageLevelLibId.KILOVOLTS_10) }
    }
}
