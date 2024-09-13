package ru.nti.dpts.schememanagerback.scheme.domain.command

import ru.nti.dpts.schememanagerback.scheme.domain.SchemeDomain
import java.util.Date

class UpdateSchemeCommand(
    val projectId: String,
    val userId: String,
    val scheme: SchemeDomain
)

class ValidateSchemeCommand(
    val projectId: String,
    val userId: String
)

class CreateProjectCommand(
    val projectId: String,
    val name: String,
    val date: Date
)

class UpdateProjectCommand(
    val projectId: String,
    val name: String
)

class DeleteProjectCommand(
    val projectId: String
)
