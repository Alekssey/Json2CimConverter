package ru.nti.dpts.schememanagerback.scheme.domain.adapter

import ru.nti.dpts.schememanagerback.scheme.domain.ProjectAggregate

interface ProjectPersister {
    fun persist(projectAggregate: ProjectAggregate)
    fun delete(projectId: String)
}
