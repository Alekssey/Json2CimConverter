package ru.nti.dpts.schememanagerback.scheme.domain.exception

class ProjectDoesNotExistsException(val projectId: String) : RuntimeException()
