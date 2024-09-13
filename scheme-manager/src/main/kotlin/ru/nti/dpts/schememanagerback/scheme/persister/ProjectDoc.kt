package ru.nti.dpts.schememanagerback.scheme.persister

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import ru.nti.dpts.schememanagerback.scheme.domain.*
import java.util.*

const val PROJECT_COLLECTION = "project"

@Document(collection = PROJECT_COLLECTION)
data class ProjectDoc(
    @Id
    val id: String,
    var name: String,
    val date: Date,
    var valid: Boolean = false,
    var scheme: SchemeDoc,
    var schemeVersionToSchemeHistoryIdMap: Map<String, String> = mapOf()
)

data class SchemeDoc(
    val locked: Boolean = false,
    val zoom: Double = 0.0,
    val offsetX: Double = 0.0,
    val offsetY: Double = 0.0,
    val version: Long = 0L,
    val metaSchemeChangeSetId: Int = 0,
    val metaSchemeVersion: String = "",
    val nodes: Map<String, EquipmentNodeDomain> = mapOf(),
    val dashboardNodes: Map<String, EquipmentNodeDomain> = mapOf(),
    val links: Map<String, EquipmentLinkDomain> = mapOf(),
    val substations: List<Substation> = listOf(),
    val transmissionLines: List<TransmissionLine> = listOf()
)

fun ProjectAggregate.convertToDoc() =
    ProjectDoc(
        this.id,
        this.name,
        this.date,
        this.valid,
        this.scheme.convertToDoc()
    )

fun ProjectDoc.convertToDomain() =
    ProjectAggregate(
        this.id,
        this.name,
        this.date,
        this.valid,
        this.scheme.convertToDomain()
    )

fun SchemeDomain.convertToDoc() = SchemeDoc(
    locked = this.locked,
    zoom = this.zoom,
    offsetX = this.offsetX,
    offsetY = this.offsetY,
    version = this.version,
    metaSchemeChangeSetId = this.metaSchemeChangeSetId,
    metaSchemeVersion = this.metaSchemeVersion,
    nodes = this.nodes,
    dashboardNodes = this.dashboardNodes,
    links = this.links,
    substations = this.substations,
    transmissionLines = this.transmissionLines
)

fun SchemeDoc.convertToDomain() = SchemeDomain(
    locked = this.locked,
    zoom = this.zoom,
    offsetX = this.offsetX,
    offsetY = this.offsetY,
    version = this.version,
    metaSchemeChangeSetId = this.metaSchemeChangeSetId,
    metaSchemeVersion = this.metaSchemeVersion,
    nodes = this.nodes + this.dashboardNodes,
    links = this.links,
    substations = this.substations,
    transmissionLines = this.transmissionLines
)
