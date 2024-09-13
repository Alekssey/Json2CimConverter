package ru.nti.dpts.schememanagerback.scheme.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import ru.nti.dpts.schememanagerback.application.common.rest.ok
import ru.nti.dpts.schememanagerback.scheme.controller.locker.SchemeEditorsHolder

@RestController
class SchemeEditorLockerController(
    private val editorsHolder: SchemeEditorsHolder
) {

    @GetMapping("/api/modeling/v3/scheme/{projectId}/editor")
    fun getUserEditor(@PathVariable projectId: String): ResponseEntity<*> {
        return ProjectEditorInfo.from(
            editorsHolder.getUserEditorByProject(projectId),
            projectId
        )
            .let { ok(it) }
    }
}

data class ProjectEditorInfo(
    val status: ProjectEditorLockStatus,
    val projectId: String,
    val usedId: String?
) {
    companion object {
        fun from(userId: String?, projectId: String): ProjectEditorInfo {
            return userId?.let {
                ProjectEditorInfo(
                    ProjectEditorLockStatus.LOCK,
                    projectId,
                    it
                )
            } ?: ProjectEditorInfo(
                ProjectEditorLockStatus.UNLOCK,
                projectId,
                null
            )
        }
    }
}

enum class ProjectEditorLockStatus {
    LOCK, UNLOCK
}
