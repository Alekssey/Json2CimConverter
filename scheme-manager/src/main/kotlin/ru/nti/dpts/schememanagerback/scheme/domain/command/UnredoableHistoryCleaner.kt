package ru.nti.dpts.schememanagerback.scheme.domain.command

interface UnredoableHistoryCleaner {
    fun clear(projectId: String)
}
