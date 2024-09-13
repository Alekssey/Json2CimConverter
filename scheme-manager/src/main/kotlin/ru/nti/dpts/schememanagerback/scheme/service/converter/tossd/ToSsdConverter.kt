package ru.nti.dpts.schememanagerback.scheme.service.converter.tossd

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.springframework.stereotype.Service
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.Substation
import ru.nti.dpts.schememanagerback.scheme.service.converter.FileByteResource
import ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.builder.ConnectivityNodeContainer
import ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.builder.SsdBuilder
import ru.nti.dpts.schememanagerback.scheme.service.graphsearch.EquipmentNodePredicate
import ru.nti.dpts.schememanagerback.scheme.service.ops.equipment.getSubstationId
import ru.nti.dpts.schememanagerback.service.converter.scl2007B4.SCL
import ru.nti.dtps.equipment.meta.info.dataclass.equipment.port.Port
import ru.nti.dtps.equipment.meta.info.manager.EquipmentMetaInfoManager
import ru.nti.dtps.proto.lib.equipment.EquipmentLibId
import java.io.StringWriter
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller

const val NO_SUBSTATION_ID = "noId"

private val transmissionLineIds = setOf(
    EquipmentLibId.TRANSMISSION_LINE_SEGMENT,
    EquipmentLibId.TRANSMISSION_LINE_SEGMENT_DOUBLE_CIRCUIT
)
val isItTransmissionLine: EquipmentNodePredicate = {
    transmissionLineIds.contains(it.libEquipmentId)
}

private val powerTransformerIds =
    setOf(
        EquipmentLibId.TWO_WINDING_POWER_TRANSFORMER,
        EquipmentLibId.THREE_WINDING_POWER_TRANSFORMER,
        EquipmentLibId.TWO_WINDING_AUTO_TRANSFORMER,
        EquipmentLibId.THREE_WINDING_AUTO_TRANSFORMER
    )
val isItPowerTransformer: EquipmentNodePredicate = {
    powerTransformerIds.contains(it.libEquipmentId)
}

typealias LinkIdAndConnectivityNodeEffect = (link: String, connectivityNodeContainer: ConnectivityNodeContainer) -> Unit
typealias GetLinkIdsAssociatedWithConnectivityNodeContainers = () -> Set<String>

@Service
class ToSsdConverter {

    private val marshaller = JAXBContext.newInstance(SCL::class.java).createMarshaller().apply {
        setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
    }

    fun convert(scheme: SchemeDomain, requiredSubstationIds: Set<String>): Either<SsdConvertError, List<FileByteResource>> {
        if (scheme.containsSinglePhaseElement()) {
            return SsdConvertError.SchemeContainsSinglePhaseElement.left()
        }

        val substationIdToEquipmentListMap = getSubstationIdToEquipmentsMap(scheme)

        return scheme.substations
            .filter { substation -> requiredSubstationIds.contains(substation.id) }
            .map { substation ->
                SsdBuilder().build(
                    scheme,
                    substation,
                    substationIdToEquipmentListMap[substation.id] ?: emptyList()
                )
                    .let { FileByteResource(writeToXmlByte(it), buildSsdFileName(substation)) }
            }.right()
    }

    private fun SchemeDomain.containsSinglePhaseElement() = this.nodes.values.any { node ->
        node.ports.any { port ->
            val portPhase = EquipmentMetaInfoManager.getPortPhasesByEquipmentLibIdAndPortLibId(
                node.libEquipmentId,
                port.libId
            )
            portPhase == Port.Phases.ONE
        }
    }

    private fun buildSsdFileName(substation: Substation) = String.format("%s.ssd", substation.name)

    private fun getSubstationIdToEquipmentsMap(
        scheme: SchemeDomain
    ): Map<String, List<EquipmentNodeDomain>> {
        val substationIds = scheme.substations.map { it.id }.toSet()
        val substationIdToEquipmentListMap =
            mutableMapOf<String, MutableList<EquipmentNodeDomain>>()
        scheme.nodes.values.asSequence()
            .filter { equipment -> substationIds.contains(equipment.getSubstationId()) }
            .forEach { equipment ->
                substationIdToEquipmentListMap
                    .computeIfAbsent(equipment.getSubstationId()) { mutableListOf() }
                    .add(equipment)
            }
        return substationIdToEquipmentListMap
    }

    private fun writeToXmlByte(scl: SCL) = StringWriter().also {
        marshaller.marshal(scl, it)
    }.toString().toByteArray()
}

sealed class SsdConvertError {
    object SchemeContainsSinglePhaseElement : SsdConvertError()
}
