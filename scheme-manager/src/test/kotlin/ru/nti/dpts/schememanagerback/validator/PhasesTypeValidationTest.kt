package ru.nti.dpts.schememanagerback.validator

import `in`.rcard.assertj.arrowcore.EitherAssert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.nti.dpts.schememanagerback.builder.EquipmentNodeBuilder
import ru.nti.dpts.schememanagerback.builder.SchemeBuilder
import ru.nti.dpts.schememanagerback.builder.SubstationBuilder
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.PhasesTypeValidator
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.SchemeContainsLinksWithInvalidPhasesConnection
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import ru.nti.dtps.proto.scheme.equipment.EquipmentNode
import java.util.*

@SpringBootTest(classes = [TestConfigurationToValidation::class])
class PhasesTypeValidationTest {

    @Autowired
    lateinit var phasesTypeValidator: PhasesTypeValidator

    private val substation = SubstationBuilder().build()
    private val busId = UUID.randomUUID().toString()
    private val powerSystemEquivalentId = UUID.randomUUID().toString()
    private val grounding1PhId = UUID.randomUUID().toString()

    private val linkIdFirstPortBus = UUID.randomUUID().toString()
    private val linkIdSecondPortBus = UUID.randomUUID().toString()

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

    private val grounding1Ph = EquipmentNodeBuilder()
        .buildGrounding1Ph()
        .withId(grounding1PhId)
        .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
        .withSubstationId(substation.id)
        .withPorts(
            listOf(
                EquipmentNodeBuilder.PortBuilder()
                    .withAlignment(EquipmentNode.Port.Alignment.TOP)
                    .withLibId(PortLibId.FIRST)
                    .withParentNode(grounding1PhId)
                    .withLinks(listOf(linkIdSecondPortBus))
                    .build()
            )
        )
        .build()

    private val nodes = mapOf(
        bus.id to bus,
        powerSystemEquivalent.id to powerSystemEquivalent,
        grounding1Ph.id to grounding1Ph
    )

    @Test
    fun `has valid onLeft error cause three-phase and single-phase connected`() {
        val scheme = SchemeBuilder()
            .withNodes(nodes)
            .connect(
                linkId = linkIdFirstPortBus,
                sourceEquipment = powerSystemEquivalent,
                targetEquipment = bus,
                sourcePort = powerSystemEquivalent.ports[0],
                targetPort = bus.ports[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .connect(
                linkId = linkIdSecondPortBus,
                sourceEquipment = grounding1Ph,
                targetEquipment = bus,
                sourcePort = grounding1Ph.ports[0],
                targetPort = bus.ports[1],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .withSubstations(listOf(substation))
            .build()

        EitherAssert.assertThat(
            phasesTypeValidator.validate(scheme)
        ).containsLeftInstanceOf(SchemeContainsLinksWithInvalidPhasesConnection::class.java)
    }
}
