package ru.nti.dpts.schememanagerback.validator

import `in`.rcard.assertj.arrowcore.EitherAssert
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.nti.dpts.schememanagerback.builder.EquipmentNodeBuilder
import ru.nti.dpts.schememanagerback.builder.SchemeBuilder
import ru.nti.dpts.schememanagerback.builder.SubstationBuilder
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.IslandsValidator
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.SchemeContainsIslandsError
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import ru.nti.dtps.proto.scheme.equipment.EquipmentNode
import java.util.*

@SpringBootTest(classes = [TestConfigurationToValidation::class])
class IslandsValidatorTest {

    @Autowired
    lateinit var islandsValidator: IslandsValidator

    private val substation = SubstationBuilder().build()

    private val busId = UUID.randomUUID().toString()
    private val bus2Id = UUID.randomUUID().toString()
    private val synchGeneratorId = UUID.randomUUID().toString()
    private val loadId = UUID.randomUUID().toString()
    private val powerSystemEquivalentId = UUID.randomUUID().toString()

    private val linkIdFirstPortBus = UUID.randomUUID().toString()
    private val linkIdSecondPortBus = UUID.randomUUID().toString()
    private val linkIdFirstPortBus2 = UUID.randomUUID().toString()

    private val bus = EquipmentNodeBuilder().buildBus()
        .withId(busId)
        .withSubstationId(substation.id)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
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
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
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
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_10)
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

    private val bus2 = EquipmentNodeBuilder().buildBus()
        .withId(bus2Id)
        .withSubstationId(substation.id)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.BOTTOM)
                    .withLibId(PortLibId.NO_ID)
                    .withParentNode(bus2Id)
                    .withLinks(listOf(linkIdFirstPortBus2))
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
                    .withLinks(listOf(linkIdFirstPortBus2))
                    .build()
            )
        )
        .build()

    @Test
    fun `validate scheme with synch generator and without islands`() {
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

        assertTrue(islandsValidator.validate(scheme).isRight())
    }

    @Test
    fun `has valid onLeft error cause scheme with synch generator has islands`() {
        val scheme = SchemeBuilder()
            .withNodes(
                mapOf(
                    bus.id to bus,
                    synchGenerator.id to synchGenerator,
                    load.id to load,
                    bus2.id to bus2,
                    powerSystemEquivalent.id to powerSystemEquivalent
                )
            )
            .connect(
                linkId = linkIdFirstPortBus,
                sourceEquipment = synchGenerator,
                targetEquipment = bus,
                sourcePort = synchGenerator.ports[0],
                targetPort = bus.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
            )
            .connect(
                linkId = linkIdSecondPortBus,
                sourceEquipment = load,
                targetEquipment = bus,
                sourcePort = load.ports[0],
                targetPort = bus.ports[1],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
            )
            .connect(
                linkId = linkIdFirstPortBus2,
                sourceEquipment = powerSystemEquivalent,
                targetEquipment = bus2,
                sourcePort = powerSystemEquivalent.ports[0],
                targetPort = bus2.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .build()

        EitherAssert.assertThat(islandsValidator.validate(scheme)).containsLeftInstanceOf(SchemeContainsIslandsError::class.java)
    }
}
