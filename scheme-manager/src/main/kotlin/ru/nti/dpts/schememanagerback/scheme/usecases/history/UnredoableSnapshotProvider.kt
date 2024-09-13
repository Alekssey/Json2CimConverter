package ru.nti.dpts.schememanagerback.scheme.usecases.history

interface UnredoableSnapshotProvider {
    fun extract(projectId: String, userId: String): UnredoableSnapshot
}
