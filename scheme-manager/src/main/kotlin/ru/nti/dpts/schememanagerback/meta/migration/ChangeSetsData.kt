package ru.nti.dpts.schememanagerback.meta.migration

import ru.nti.dpts.schememanagerback.meta.migration.ops.loadChangeLogContentFromResource
import ru.nti.dpts.schememanagerback.meta.migration.ops.loadChangeSets

private const val CHANGELOG_RESOURCE_PATH = "meta/scheme/changelog/changelog.yaml"

object ChangeSetsData {
    val changeSets: List<ChangeLog.ChangeSet> by lazy {
        loadChangeSets(loadChangeLogContentFromResource(CHANGELOG_RESOURCE_PATH))
    }

    val actualChangeSetId: Int by lazy { changeSets.last().id }

    val relatedMetaSchemeVersion: String by lazy { changeSets.last().relatedMetaSchemeVersion }
}
