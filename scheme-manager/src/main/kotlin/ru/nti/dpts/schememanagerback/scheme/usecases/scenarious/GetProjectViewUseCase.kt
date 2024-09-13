package ru.nti.dpts.schememanagerback.scheme.usecases.scenarious

import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectExtractor
import ru.nti.dpts.schememanagerback.scheme.usecases.GetProjectView
import ru.nti.dpts.schememanagerback.scheme.usecases.ProjectView
import ru.nti.dpts.schememanagerback.scheme.usecases.SchemeView
import ru.nti.dpts.schememanagerback.scheme.usecases.history.UnredoableSnapshotProvider

@Component
class GetProjectViewUseCase(
//    private val projectExtractor: ProjectExtractor,
//    private val unredoableSnapshotProvider: UnredoableSnapshotProvider
) {
//    override fun execute(projectId: String, userId: String): ProjectView {
//        return projectExtractor.extract(projectId).let {
//            ProjectView(
//                it.id,
//                it.name,
//                it.date,
//                it.valid,
//                SchemeView.create(it.scheme),
//                unredoableSnapshotProvider.extract(projectId, userId)
//            )
//        }
//    }
}
