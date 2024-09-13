package ru.nti.dpts.schememanagerback.scheme.usecases

import arrow.core.Either
import org.springframework.web.multipart.MultipartFile
import ru.nti.dpts.schememanagerback.scheme.domain.*
import ru.nti.dpts.schememanagerback.scheme.service.converter.FileByteResource

interface CimSchemeConverter {
    fun fromCim(
        userId: String,
        projectId: String,
        file: MultipartFile
    ): Either<CimSchemeConverterError, ProjectAggregate>

    fun toCim(projectId: String): Either<CimSchemeConverterError, FileByteResource>
}

sealed class CimSchemeConverterError {
    object SchemeNotValidError : CimSchemeConverterError()
    object CimFileNotValidError : CimSchemeConverterError()
    object DataInCimFileNotValidError : CimSchemeConverterError()
    class UnknownCimConverterError(val message: String) : CimSchemeConverterError()
}

data class SchemeCimConverterDto(
    val locked: Boolean = false,
    val zoom: Double = 0.0,
    val offsetX: Double = 0.0,
    val offsetY: Double = 0.0,
    val version: Long = 0L,
    val metaSchemeChangeSetId: Int = 0,
    val metaSchemeVersion: String = "",
    val nodes: Map<String, EquipmentNodeDomain> = mapOf(),
    val links: Map<String, EquipmentLinkDomain> = mapOf(),
    val dashboardNodes: Map<String, EquipmentLinkDomain> = mapOf(),
    val substations: List<Substation> = listOf(),
    val transmissionLines: List<TransmissionLine> = listOf()
)

fun SchemeDomain.convertToCimDto() = SchemeCimConverterDto(
    locked = this.locked,
    zoom = this.zoom,
    offsetX = this.offsetX,
    offsetY = this.offsetY,
    version = this.version,
    metaSchemeChangeSetId = this.metaSchemeChangeSetId,
    metaSchemeVersion = this.metaSchemeVersion,
    nodes = this.nodes,
    links = this.links,
    substations = this.substations,
    transmissionLines = this.transmissionLines
)

fun SchemeCimConverterDto.convertToDomain() = SchemeDomain(
    locked = this.locked,
    zoom = this.zoom,
    offsetX = this.offsetX,
    offsetY = this.offsetY,
    version = this.version,
    metaSchemeChangeSetId = this.metaSchemeChangeSetId,
    metaSchemeVersion = this.metaSchemeVersion,
    nodes = this.nodes,
    links = this.links,
    substations = this.substations,
    transmissionLines = this.transmissionLines
)
