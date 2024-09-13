package ru.nti.dpts.schememanagerback.scheme.domain.adapter

import ru.nti.dpts.schememanagerback.scheme.domain.ProjectAggregate

interface LastValidProjectSender {
    fun send(projectAggregate: ProjectAggregate)
}
