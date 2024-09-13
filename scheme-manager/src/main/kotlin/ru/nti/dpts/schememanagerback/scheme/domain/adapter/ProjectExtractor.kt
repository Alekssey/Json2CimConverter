package ru.nti.dpts.schememanagerback.scheme.domain.adapter

import ru.nti.dpts.schememanagerback.scheme.domain.ProjectAggregate

interface ProjectExtractor {
    fun extract(projectId: String): ProjectAggregate
}
