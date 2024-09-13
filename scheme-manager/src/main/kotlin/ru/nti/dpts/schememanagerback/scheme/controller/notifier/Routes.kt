package ru.nti.dpts.schememanagerback.scheme.controller.notifier

class Routes {
    companion object {
        fun publishCommand(projectId: String) = "/queue/scheme.$projectId.command"
        fun publishUser(userId: String) = "/user/queue.error"
        fun publishSchemeStatus(projectId: String) = "/queue/scheme.$projectId.status"
        fun publishUnredoable(projectId: String) = "/queue/scheme.$projectId.unredoable"
    }
}
