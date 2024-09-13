package ru.nti.dpts.schememanagerback.builder

import org.slf4j.LoggerFactory
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentLinkDomain
import ru.nti.dpts.schememanagerback.scheme.domain.EquipmentNodeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import ru.nti.dpts.schememanagerback.scheme.domain.Substation
import ru.nti.dpts.schememanagerback.scheme.domain.TransmissionLine
import ru.nti.dtps.proto.lib.voltage.VoltageLevelLibId

data class SchemeBuilder(
    private var locked: Boolean = false,
    private var zoom: Double = 0.0,
    private var offsetX: Double = 0.0,
    private var offsetY: Double = 0.0,
    private var version: Long = 0L,
    private var metaSchemeChangeSetId: Int = 0,
    private var metaSchemeVersion: String = "",
    private var nodes: MutableMap<String, EquipmentNodeDomain> = mutableMapOf(),
    private var links: MutableMap<String, EquipmentLinkDomain> = mutableMapOf(),
    private var substations: MutableList<Substation> = mutableListOf(),
    private var transmissionLines: MutableList<TransmissionLine> = mutableListOf()
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun withLocked(locked: Boolean) = apply { this.locked = locked }
    fun withOffsetX(offsetX: Double) = apply { this.offsetX = offsetX }
    fun withOffsetY(offsetY: Double) = apply { this.offsetY = offsetY }
    fun withVersion(version: Long) = apply { this.version = version }
    fun withMetaSchemeChangeSetId(metaSchemeChangeSetId: Int) = apply {
        this.metaSchemeChangeSetId = metaSchemeChangeSetId
    }
    fun withMetaSchemeVersion(metaSchemeVersion: String) = apply { this.metaSchemeVersion = metaSchemeVersion }
    fun withNodes(nodes: Map<String, EquipmentNodeDomain>) = apply { this.nodes.putAll(nodes) }
    fun withLinks(links: Map<String, EquipmentLinkDomain>) = apply { this.links.putAll(links) }
    fun withSubstations(substations: List<Substation>) = apply { this.substations.addAll(substations) }
    fun withTransmissionLines(transmissionLines: List<TransmissionLine>) = apply {
        this.transmissionLines.addAll(transmissionLines)
    }

    fun build() = SchemeDomain(
        locked,
        zoom,
        offsetX,
        offsetY,
        version,
        metaSchemeChangeSetId,
        metaSchemeVersion,
        nodes,
        links,
        substations,
        transmissionLines
    )

    fun connect(
        sourceEquipment: EquipmentNodeDomain,
        sourcePort: EquipmentNodeDomain.PortDto,
        targetEquipment: EquipmentNodeDomain,
        targetPort: EquipmentNodeDomain.PortDto,
        linkId: String,
        voltageLevelLibId: VoltageLevelLibId?
    ) = apply {
        if (sourcePort.links.contains(linkId) && targetPort.links.contains(linkId)) {
            val linkBetweenSourceAndTargetEquipment = EquipmentLinkBuilder()
                .withId(linkId)
                .withSource(sourceEquipment.id)
                .withTarget(targetEquipment.id)
                .withSourcePort(sourcePort.id)
                .withTargetPort(targetPort.id)
                .withVoltageLeveId(voltageLevelLibId)
                .build()

            this.links.putAll(
                mapOf(
                    linkBetweenSourceAndTargetEquipment.id to linkBetweenSourceAndTargetEquipment
                )
            )
        } else {
            log.debug("Equipment ${sourceEquipment.id} and equipment ${targetEquipment.id} can not be connected cause they have not common link")
        }
    }
}
