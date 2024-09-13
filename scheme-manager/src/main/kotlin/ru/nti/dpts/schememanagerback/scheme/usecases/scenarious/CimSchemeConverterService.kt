package ru.nti.dpts.schememanagerback.scheme.usecases.scenarious

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import ru.nti.dpts.schememanagerback.scheme.domain.ProjectAggregate
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectExtractor
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectPersister
import ru.nti.dpts.schememanagerback.scheme.domain.command.UnredoableHistoryCleaner
import ru.nti.dpts.schememanagerback.scheme.service.converter.FileByteResource
import ru.nti.dpts.schememanagerback.scheme.service.converter.zip
import ru.nti.dpts.schememanagerback.scheme.usecases.*
import ru.nti.dtps.cimconverter.CimConverter
import ru.nti.dtps.cimconverter.fromcim.exception.CimDataException
import ru.nti.dtps.cimconverter.fromcim.exception.RdfFileException

//@Component
class CimSchemeConverterService(
    private val projectExtractor: ProjectExtractor,
    private val projectPersister: ProjectPersister,
    private val unredoableHistoryCleaner: UnredoableHistoryCleaner
) : CimSchemeConverter {

    companion object {
        private val logger = LoggerFactory.getLogger(CimSchemeConverter::class.java)
    }

    private val objectMapper: ObjectMapper = jacksonObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)

    override fun fromCim(
        userId: String,
        projectId: String,
        file: MultipartFile
    ): Either<CimSchemeConverterError, ProjectAggregate> {
        return Either.catch {
            val schemeFromCimAsString = CimConverter.fromCim(file.inputStream)
            objectMapper.readValue<SchemeCimConverterDto>(schemeFromCimAsString)
        }.getOrElse {
            logger.error("Error while convert cim to scheme cause: {}", it.message, it)
            return when (it) {
                is CimDataException -> CimSchemeConverterError.DataInCimFileNotValidError.left()
                is RdfFileException -> CimSchemeConverterError.CimFileNotValidError.left()
                else -> CimSchemeConverterError.UnknownCimConverterError(it.localizedMessage).left()
            }
        }.let {
            projectExtractor.extract(projectId)
                .apply {
                    this.updateSchemeWhileCimConverter(unredoableHistoryCleaner, it.convertToDomain())
                }
                .let {
                    projectPersister.persist(it)
                    it.right()
                }
        }
    }

    override fun toCim(projectId: String): Either<CimSchemeConverterError, FileByteResource> {
        val projectAggregate = projectExtractor
            .extract(projectId)

        if (!projectAggregate.valid) {
            return CimSchemeConverterError.SchemeNotValidError.left()
        }
        val cim = CimConverter.toCim(objectMapper.writeValueAsString(projectAggregate.scheme.convertToCimDto()))
        return FileByteResource(
            data = cim.toByteArray(),
            fileName = "${projectAggregate.name}.xml"
        ).let {
            zip("${projectAggregate.name}.zip", it)
        }.right()
    }
}
