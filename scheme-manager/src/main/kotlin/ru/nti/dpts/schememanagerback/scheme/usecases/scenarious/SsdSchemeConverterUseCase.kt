package ru.nti.dpts.schememanagerback.scheme.usecases.scenarious

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectExtractor
import ru.nti.dpts.schememanagerback.scheme.service.converter.FileByteResource
import ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.SsdConvertError
import ru.nti.dpts.schememanagerback.scheme.service.converter.tossd.ToSsdConverter
import ru.nti.dpts.schememanagerback.scheme.service.converter.zip
import ru.nti.dpts.schememanagerback.scheme.usecases.ConvertSchemeToSsdCommand
import ru.nti.dpts.schememanagerback.scheme.usecases.SsdSchemeConverter
import ru.nti.dpts.schememanagerback.scheme.usecases.SsdSchemeConverterUseCaseError

@Component
class SsdSchemeConverterUseCase(
    private val ssdConverter: ToSsdConverter,
//    private val projectExtractor: ProjectExtractor
) {

//    override fun handle(command: ConvertSchemeToSsdCommand): Either<SsdSchemeConverterUseCaseError, FileByteResource> {
//        val projectAggregate = projectExtractor
//            .extract(command.projectId)
//
//        if (!projectAggregate.valid) {
//            return SsdSchemeConverterUseCaseError.SchemeNotValidUseCaseUseCaseError.left()
//        }
//
//        return ssdConverter.convert(projectAggregate.scheme, command.substationIds)
//            .getOrElse {
//                return when (it) {
//                    SsdConvertError.SchemeContainsSinglePhaseElement -> SsdSchemeConverterUseCaseError.SchemeContainSinglePhaseElementUseCaseUseCaseError.left()
//                }
//            }.let {
//                zip("${projectAggregate.name}.zip", *it.toTypedArray())
//            }.right()
//    }
}
