package ru.nti.dpts.schememanagerback.scheme.usecases.scenarious

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.springframework.stereotype.Service
import ru.nti.dpts.schememanagerback.application.common.error.BusinessError
import ru.nti.dpts.schememanagerback.scheme.domain.Substation
import ru.nti.dpts.schememanagerback.scheme.domain.SubstationContainsEquipmentsError
import ru.nti.dpts.schememanagerback.scheme.domain.SubstationDuplicationError
import ru.nti.dpts.schememanagerback.scheme.domain.SubstationNotFoundError
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectExtractor
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectPersister

@Service
class SubstationService(
//    private val projectExtractor: ProjectExtractor,
//    private val projectPersister: ProjectPersister
) {

//    fun createSubstation(
//        substationCreateDto: SubstationCreateDto
//    ): Either<SubstationManagerError, Substation> {
//        return projectExtractor.extract(substationCreateDto.projectId).let { projectAggregate ->
//            projectAggregate.scheme
//                .createSubstation(substationCreateDto.name)
//                .fold(
//                    { it.toError().left() },
//                    { substation ->
//                        projectPersister.persist(projectAggregate)
//                        substation.right()
//                    }
//                )
//        }
//    }
//
//    fun deleteSubstationIfEmpty(
//        projectId: String,
//        substationId: String
//    ): Either<SubstationManagerError, Substation> {
//        return projectExtractor.extract(projectId).let { projectAggregate ->
//            projectAggregate.scheme
//                .removeSubstation(substationId)
//                .fold(
//                    { it.toError().left() },
//                    { substation ->
//                        projectPersister.persist(projectAggregate)
//                        substation.right()
//                    }
//                )
//        }
//    }

    private fun BusinessError.toError(): SubstationManagerError {
        return when (this) {
            is SubstationNotFoundError -> SubstationManagerError.SubstationNotFoundError
            is SubstationDuplicationError -> SubstationManagerError.SubstationDuplicationError(this.name)
            is SubstationContainsEquipmentsError -> SubstationManagerError.SubstationContainsEquipmentsError(this.name)
            else -> SubstationManagerError.UnknownError
        }
    }
}

data class SubstationCreateDto(
    val projectId: String,
    val name: String
)

sealed class SubstationManagerError {
    class SubstationDuplicationError(val substationName: String) : SubstationManagerError()
    object SubstationNotFoundError : SubstationManagerError()
    class SubstationContainsEquipmentsError(val substationName: String) : SubstationManagerError()
    object UnknownError : SubstationManagerError()
}
