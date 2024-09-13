package ru.nti.dpts.schememanagerback.scheme.usecases.scenarious

import org.springframework.stereotype.Service
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectExtractor
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectPersister
import ru.nti.dpts.schememanagerback.scheme.domain.command.UnredoableHistoryCleaner
import ru.nti.dpts.schememanagerback.scheme.domain.command.UpdateSchemeCommand
import ru.nti.dpts.schememanagerback.scheme.usecases.UpdateScheme

@Service
class UpdateSchemeUseCase(
//    private val projectExtractor: ProjectExtractor,
//    private val projectPersister: ProjectPersister,
//    private val unredoableHistoryCleaner: UnredoableHistoryCleaner
)  {
//    override fun execute(updateSchemeCommand: UpdateSchemeCommand) {
//        projectExtractor.extract(updateSchemeCommand.projectId).let { projectAggregate ->
//            projectAggregate.updateSchemeFromInternal(
//                updateSchemeCommand.scheme,
//                updateSchemeCommand.userId,
//                unredoableHistoryCleaner
//            )
//            projectPersister.persist(projectAggregate)
//        }
//    }
}
