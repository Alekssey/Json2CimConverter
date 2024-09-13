package ru.nti.dpts.schememanagerback.scheme.usecases

import ru.nti.dpts.schememanagerback.scheme.domain.command.CreateProjectCommand
import ru.nti.dpts.schememanagerback.scheme.domain.command.DeleteProjectCommand
import ru.nti.dpts.schememanagerback.scheme.domain.command.UpdateProjectCommand

interface DtpsProjectService {
    fun handle(command: CreateProjectCommand)
    fun handle(command: UpdateProjectCommand)
    fun handle(command: DeleteProjectCommand)
}
