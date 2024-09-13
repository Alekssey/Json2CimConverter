package ru.nti.dpts.schememanagerback.scheme.usecases

import ru.nti.dpts.schememanagerback.scheme.domain.*
import ru.nti.dpts.schememanagerback.scheme.usecases.history.UnredoableSnapshot
import java.util.*

interface GetProjectView {
    fun execute(projectId: String, userId: String): ProjectView
}

data class ProjectView internal constructor(
    val id: String,
    val name: String,
    val date: Date,
    var valid: Boolean = false,
    val scheme: SchemeView,
    val unredoable: UnredoableSnapshot
)

data class SchemeView(
    val locked: Boolean,
    val zoom: Double,
    val offsetX: Double,
    val offsetY: Double,
    var version: Long,
    val metaSchemeChangeSetId: Int,
    val metaSchemeVersion: String,
    val nodes: Map<String, EquipmentNodeDomain>,
    val links: Map<String, EquipmentLinkDomain>,
    val substations: List<Substation>,
    val transmissionLines: List<TransmissionLine>
) {

    companion object {
        fun create(domain: SchemeDomain) = SchemeView(
            locked = domain.locked,
            zoom = domain.zoom,
            version = domain.version,
            offsetX = domain.offsetX,
            offsetY = domain.offsetY,
            metaSchemeChangeSetId = domain.metaSchemeChangeSetId,
            metaSchemeVersion = domain.metaSchemeVersion,
            nodes = domain.nodes + domain.dashboardNodes,
            links = domain.links,
            substations = domain.substations,
            transmissionLines = domain.transmissionLines
        )
    }
    fun toDomain() = SchemeDomain(
        locked = locked,
        zoom = zoom,
        offsetX = offsetX,
        offsetY = offsetY,
        version = version,
        metaSchemeChangeSetId = metaSchemeChangeSetId,
        metaSchemeVersion = metaSchemeVersion,
        nodes = nodes,
        links = links,
        substations = substations,
        transmissionLines = transmissionLines
    )
}
