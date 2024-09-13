package ru.nti.dpts.schememanagerback.scheme.usecases.command

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.scheme.domain.CommandExecutionError
import ru.nti.dpts.schememanagerback.scheme.domain.ProjectAggregate
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectExtractor
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectPersister
import ru.nti.dpts.schememanagerback.scheme.domain.command.Command
import ru.nti.dpts.schememanagerback.scheme.domain.command.SchemeEditorCommandExecutor

@Component
class BaseCommandExecutor(
//    val projectExtractor: ProjectExtractor,
//    val projectPersister: ProjectPersister
) {

    companion object {
        private val logger = LoggerFactory.getLogger(SchemeEditorCommandExecutor::class.java)
    }

//    override fun execute(command: Command): Either<CommandExecutionError, ProjectAggregate> {
//        val aggregate = projectExtractor.extract(command.projectId)
//        return aggregate.execute(command).fold(
//            {
//                logger.debug("Execution of command {} failed cause {} ", command, it)
//                it.left()
//            },
//            {
//                projectPersister.persist(aggregate)
//                aggregate.right()
//            }
//        )
//    }
}
