package ru.nti.dpts.schememanagerback.scheme.usecases.scenarious

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.springframework.stereotype.Service
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.LastValidProjectSender
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectExtractor
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectPersister
import ru.nti.dpts.schememanagerback.scheme.domain.command.ValidateSchemeCommand
import ru.nti.dpts.schememanagerback.scheme.service.augmentation.AugmentationService
import ru.nti.dpts.schememanagerback.scheme.service.validator.ComplexSchemeValidator
import ru.nti.dpts.schememanagerback.scheme.service.validator.scheme.ValidationError
import ru.nti.dpts.schememanagerback.scheme.usecases.ValidateScheme

//@Service
class ValidateSchemeUseCase(
    private val projectExtractor: ProjectExtractor,
    private val projectPersister: ProjectPersister,
    private val complexSchemeValidator: ComplexSchemeValidator,
    private val augmentationService: AugmentationService,
    private val lastValidProjectSender: LastValidProjectSender
) : ValidateScheme {
    override fun execute(command: ValidateSchemeCommand): Either<ValidationError, Unit> {
        return projectExtractor.extract(command.projectId).let { projectAggregate ->
            projectAggregate
                .validate(complexSchemeValidator, augmentationService)
                .fold(
                    { it.left() },
                    {
                        lastValidProjectSender.send(projectAggregate)
                        projectPersister.persist(projectAggregate).right()
                    }
                )
        }
    }
}
