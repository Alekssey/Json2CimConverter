package ru.nti.dpts.schememanagerback.converter.ssd

import arrow.core.getOrElse
import arrow.core.left
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import ru.nti.dpts.schememanagerback.builder.EquipmentNodeBuilder
import ru.nti.dpts.schememanagerback.builder.SchemesForTest
import ru.nti.dpts.schememanagerback.builder.SubstationBuilder
import ru.nti.dpts.schememanagerback.scheme.service.converter.FileByteResource
import ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.SsdConvertError
import ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.ToSsdConverter
import ru.nti.dpts.schememanagerback.service.converter.scl2007B4.SCL
import ru.nti.dtps.proto.lib.port.PortLibId
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId
import ru.nti.dtps.proto.scheme.equipment.EquipmentNode
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import java.util.*
import javax.xml.bind.JAXBContext
import javax.xml.bind.Unmarshaller

private val unmarshaller: Unmarshaller = JAXBContext.newInstance(SCL::class.java).createUnmarshaller()

private val unmarshallToScl: (scl: String) -> SCL = { scl ->
    unmarshaller.unmarshal(ByteArrayInputStream(scl.toByteArray())) as SCL
}
private const val SUBSTATION_NAME = "substation 500_220_10 _ _ _ _ _ _ _"

class ConvertSchemeToSSD {

    @Test
    fun `substation to SSD conversion`() {
        val scheme = SchemesForTest.buildSchemeWithAllThreePhaseEquipment()

        val ssdFileList: List<FileByteResource> = ToSsdConverter().convert(scheme, setOf(scheme.substations[0].id))
            .getOrElse { error("Test precondition error") }

        Assertions.assertThat(ssdFileList).hasSize(1)
        val resultSCL: SCL = unmarshallToScl(ssdFileList.first().data.toString(Charset.defaultCharset()))

        Assertions.assertThat(resultSCL.substation.first().voltageLevel).hasSize(9)
        Assertions.assertThat(resultSCL.substation.first().voltageLevel[0].voltage.value.toInt()).isEqualTo(110)
        Assertions.assertThat(resultSCL.substation.first().voltageLevel[1].voltage.value.toInt()).isEqualTo(10)
        Assertions.assertThat(resultSCL.substation.first().voltageLevel[2].voltage.value.toInt()).isEqualTo(35)
        Assertions.assertThat(resultSCL.substation.first().name).isEqualTo(SUBSTATION_NAME)
    }

    @Test
    fun `substation with CN to SSD conversion`() {
        val scheme = SchemesForTest.buildSchemeWithAllThreePhaseEquipment()

        val ssdFileList: List<FileByteResource> =
            ToSsdConverter().convert(scheme, setOf(scheme.substations[0].id))
                .getOrElse { error("Test precondition error") }
        Assertions.assertThat(ssdFileList).hasSize(1)
        val resultSCL: SCL = unmarshallToScl(ssdFileList.first().data.toString(Charset.defaultCharset()))
        val connectivityNodesCounter = resultSCL.substation.flatMap { voltageLevelContainer ->
            voltageLevelContainer.voltageLevel.flatMap { bayContainer ->
                bayContainer.bay.flatMap { it.connectivityNode }
            }
        }
        Assertions.assertThat(connectivityNodesCounter.size).isEqualTo(17)
    }

    @Test
    fun `substation with three winding power transformer`() {
        val substation = SubstationBuilder().build()

        val busId = UUID.randomUUID().toString()
        val linkIdFirstPortBus = UUID.randomUUID().toString()
        val linkIdSecondPortBus = UUID.randomUUID().toString()
        val bus = EquipmentNodeBuilder().buildBus()
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

        val loadId = UUID.randomUUID().toString()
        val linkIdFirstPortLoad = UUID.randomUUID().toString()
        val load = EquipmentNodeBuilder()
            .buildLoad()
            .withId(loadId)
            .withVoltageLevelId(VoltageLevelLibId.KILOVOLTS_110)
            .withSubstationId(substation.id)
            .withPorts(
                listOf(
                    EquipmentNodeBuilder.PortBuilder()
                        .withAlignment(EquipmentNode.Port.Alignment.TOP)
                        .withLibId(PortLibId.FIRST)
                        .withParentNode(loadId)
                        .withLinks(listOf(linkIdFirstPortLoad))
                        .build()
                )
            )
            .build()

        val powerSystemEquivalentId = UUID.randomUUID().toString()
        val powerSystemEquivalent = EquipmentNodeBuilder()
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

        val scheme = SchemesForTest
            .buildSchemeWithThreeWindingPowerTransformerAndLoadOnLowVoltageSide(
                substation,
                targetEquipmentHighVoltageSide = bus,
                targetPortHighVoltageSide = bus.ports[1],
                linkIdHighVoltageSide = bus.ports[1].links[0],
                targetEquipmentMiddleVoltageSide = load,
                targetPortMiddleVoltageSide = load.ports[0],
                linkIdMiddleVoltageSide = linkIdFirstPortLoad,
                highVoltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110,
                middleVoltageLevelLibId = VoltageLevelLibId.KILOVOLTS_35,
                lowVoltageLevelLibId = VoltageLevelLibId.KILOVOLTS_10
            )
            .withNodes(
                mapOf(
                    bus.id to bus,
                    powerSystemEquivalent.id to powerSystemEquivalent,
                    load.id to load
                )
            )
            .connect(
                sourceEquipment = powerSystemEquivalent,
                sourcePort = powerSystemEquivalent.ports[0],
                targetEquipment = bus,
                targetPort = bus.ports[0],
                linkId = bus.ports[0].links[0],
                voltageLevelLibId = VoltageLevelLibId.KILOVOLTS_110
            )
            .withSubstations(listOf(substation))
            .build()

        val ssdFileList: List<FileByteResource> =
            ToSsdConverter().convert(scheme, setOf(scheme.substations[0].id))
                .getOrElse { error("Test precondition error") }
        Assertions.assertThat(ssdFileList).hasSize(1)
        val resultSCL: SCL = unmarshallToScl(ssdFileList.first().data.toString(Charset.defaultCharset()))
        Assertions.assertThat(resultSCL.substation.first().voltageLevel).hasSize(3)
        Assertions.assertThat(resultSCL.substation.first().voltageLevel[0].voltage.value.toInt()).isEqualTo(110)
        Assertions.assertThat(resultSCL.substation.first().voltageLevel[1].voltage.value.toInt()).isEqualTo(35)
        Assertions.assertThat(resultSCL.substation.first().voltageLevel[2].voltage.value.toInt()).isEqualTo(10)
    }

    @Test
    fun `throw exception cause scheme contains one phase elements`() {
        val scheme = SchemesForTest.buildSchemeWithAllTypesEquipment()
        val substationIds = scheme.substations.map { it.id }.toSet()

        Assertions.assertThat(ToSsdConverter().convert(scheme, substationIds))
            .isEqualTo(SsdConvertError.SchemeContainsSinglePhaseElement.left())
    }
}
