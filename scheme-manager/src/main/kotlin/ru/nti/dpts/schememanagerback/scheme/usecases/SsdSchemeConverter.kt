package ru.nti.dpts.schememanagerback.scheme.usecases

import arrow.core.Either
import ru.nti.dpts.schememanagerback.scheme.service.converter.FileByteResource

interface SsdSchemeConverter {
    /*Return already zipped file or error*/
    fun handle(command: ConvertSchemeToSsdCommand): Either<SsdSchemeConverterUseCaseError, FileByteResource>
}

data class ConvertSchemeToSsdCommand(
    val projectId: String,
    val substationIds: Set<String>
)

sealed class SsdSchemeConverterUseCaseError {
    object SchemeNotValidUseCaseUseCaseError : SsdSchemeConverterUseCaseError()
    object SchemeContainSinglePhaseElementUseCaseUseCaseError : SsdSchemeConverterUseCaseError()
}
