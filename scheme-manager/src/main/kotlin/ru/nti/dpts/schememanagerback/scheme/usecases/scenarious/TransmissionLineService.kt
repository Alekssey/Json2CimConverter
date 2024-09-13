package ru.nti.dpts.schememanagerback.scheme.usecases.scenarious

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.springframework.stereotype.Service
import ru.nti.dpts.schememanagerback.application.common.error.BusinessError
import ru.nti.dpts.schememanagerback.scheme.domain.TransmissionLine
import ru.nti.dpts.schememanagerback.scheme.domain.TransmissionLineContainsSegmentsError
import ru.nti.dpts.schememanagerback.scheme.domain.TransmissionLineDuplicationError
import ru.nti.dpts.schememanagerback.scheme.domain.TransmissionLineNotFoundError
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectExtractor
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectPersister

const val NO_ID_TRANSMISSION_LINE = "noId"

@Service
class TransmissionLineService(
//    private val projectExtractor: ProjectExtractor,
//    private val projectPersister: ProjectPersister
) {

//    fun createTransmissionLine(
//        transmissionLineCreateDto: TransmissionLineCreateDto
//    ): Either<TransmissionLineManagerError, TransmissionLine> {
//        return projectExtractor.extract(transmissionLineCreateDto.projectId).let { projectAggregate ->
//            projectAggregate.scheme
//                .createTransmissionLine(transmissionLineCreateDto.name)
//                .fold(
//                    { it.toError().left() },
//                    { transmissionLine ->
//                        projectPersister.persist(projectAggregate)
//                        transmissionLine.right()
//                    }
//                )
//        }
//    }
//
//    fun deleteTransmissionLine(
//        projectId: String,
//        transmissionLineId: String
//    ): Either<TransmissionLineManagerError, TransmissionLine> {
//        return projectExtractor.extract(projectId).let { projectAggregate ->
//            projectAggregate.scheme
//                .removeTransmissionLine(transmissionLineId)
//                .fold(
//                    { it.toError().left() },
//                    { transmissionLine ->
//                        projectPersister.persist(projectAggregate)
//                        transmissionLine.right()
//                    }
//                )
//        }
//    }

    private fun BusinessError.toError(): TransmissionLineManagerError {
        return when (this) {
            is TransmissionLineNotFoundError -> TransmissionLineManagerError.TransmissionLineNotFoundError
            is TransmissionLineDuplicationError -> TransmissionLineManagerError.TransmissionLineDuplicationError(this.name)
            is TransmissionLineContainsSegmentsError -> TransmissionLineManagerError.TransmissionLineContainsSegmentsError(this.name)
            else -> TransmissionLineManagerError.UnknownError
        }
    }
}

data class TransmissionLineCreateDto(
    val projectId: String,
    val name: String
)

sealed class TransmissionLineManagerError {
    class TransmissionLineDuplicationError(val transmissionLineName: String) : TransmissionLineManagerError()
    object TransmissionLineNotFoundError : TransmissionLineManagerError()
    class TransmissionLineContainsSegmentsError(val transmissionLineName: String) : TransmissionLineManagerError()
    object UnknownError : TransmissionLineManagerError()
}
