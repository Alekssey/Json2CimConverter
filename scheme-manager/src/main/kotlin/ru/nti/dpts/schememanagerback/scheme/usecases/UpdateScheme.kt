package ru.nti.dpts.schememanagerback.scheme.usecases

import ru.nti.dpts.schememanagerback.scheme.domain.command.UpdateSchemeCommand

interface UpdateScheme {
    fun execute(updateSchemeCommand: UpdateSchemeCommand)
}
