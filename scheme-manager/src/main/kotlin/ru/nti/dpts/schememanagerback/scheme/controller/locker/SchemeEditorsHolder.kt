package ru.nti.dpts.schememanagerback.scheme.controller.locker

interface SchemeEditorsHolder {
    /*
    * Return user id if project is tracked by user and null if not
    * */
    fun getUserEditorByProject(projectId: String): String?

    /*
    * Update project editor or throw ProjectAlreadyEditByAnotherUserException.class exception
    * */
    fun updateProjectEditorOrThrowException(projectId: String, userId: String)
}

class ProjectAlreadyEditByAnotherUserException : RuntimeException()
