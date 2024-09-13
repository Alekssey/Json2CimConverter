package ru.nti.dpts.schememanagerback.scheme.usecases.scenarious

import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.scheme.domain.ProjectAggregate
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectExtractor
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectPersister
import ru.nti.dpts.schememanagerback.scheme.domain.command.CreateProjectCommand
import ru.nti.dpts.schememanagerback.scheme.domain.command.DeleteProjectCommand
import ru.nti.dpts.schememanagerback.scheme.domain.command.UpdateProjectCommand
import ru.nti.dpts.schememanagerback.scheme.usecases.DtpsProjectService
import java.time.Instant
import java.util.*

@Component
class ProjectService(
//    private val projectExtractor: ProjectExtractor,
//    private val projectPersister: ProjectPersister
)  {

//    override fun handle(command: CreateProjectCommand) {
//        ProjectAggregate.create(command.projectId, command.name, command.date).apply {
//            projectPersister.persist(this)
//        }
//    }
//
//    override fun handle(command: UpdateProjectCommand) {
//        try {
//            projectExtractor.extract(command.projectId)
//                .let { projectAggregate ->
//                    projectAggregate.update(command.name)
//                    projectPersister.persist(projectAggregate)
//                }
//        } catch (e: Exception) {
//            ProjectAggregate.create(
//                command.projectId,
//                command.name,
//                Date.from(Instant.now())
//            ).apply { projectPersister.persist(this) }
//        }
//    }
//
//    override fun handle(command: DeleteProjectCommand) {
//        projectPersister.delete(command.projectId)
//    }
}
