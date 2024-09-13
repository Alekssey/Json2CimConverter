package ru.nti.dpts.schememanagerback.meta.migration.ops

import org.bson.Document
import ru.nti.dpts.schememanagerback.meta.migration.ChangeLog
import ru.nti.dpts.schememanagerback.meta.migration.ChangeSetsData
import ru.nti.dpts.schememanagerback.meta.migration.docfields.SchemeFields
import ru.nti.dpts.schememanagerback.meta.migration.ops.mutation.*

fun mutateScheme(
    scheme: Document,
    changeSets: List<ChangeLog.ChangeSet>
): Boolean {
    val schemeChangeSetId: Int = scheme[SchemeFields.metaSchemeChangeSetId]?.toString()?.toInt() ?: 0
    val schemeSpecificChangeSets = getSchemeSpecificChangeSets(changeSets, schemeChangeSetId)

    mutateSchemeByChangeSets(scheme, schemeSpecificChangeSets)

    return schemeSpecificChangeSets.isNotEmpty()
}

private fun getSchemeSpecificChangeSets(
    changeSets: List<ChangeLog.ChangeSet>,
    schemeChangeSetId: Int
): List<ChangeLog.ChangeSet> {
    return changeSets.filter { changeSet -> changeSet.id > schemeChangeSetId }
}

private fun mutateSchemeByChangeSets(scheme: Document, changeSets: List<ChangeLog.ChangeSet>) {
    changeSets.forEach { mutateSchemeByChangeSet(scheme, it) }
    scheme[SchemeFields.metaSchemeChangeSetId] = ChangeSetsData.actualChangeSetId
    scheme[SchemeFields.metaSchemeVersion] = ChangeSetsData.relatedMetaSchemeVersion
}

private fun mutateSchemeByChangeSet(scheme: Document, changeSet: ChangeLog.ChangeSet) {
    changeSet.changes.forEach { change ->
        change.replaceKey?.let { replaceKey(scheme, it) }
        change.replaceValue?.let { replaceValue(scheme, it) }
        change.removeEntry?.let { removeEntry(scheme, it) }
        change.moveValueToChildNodeField?.let { moveValueToChildNodeField(scheme, it) }
        change.removeChildEntriesMatching?.let { removeChildEntriesMatching(scheme, it) }
        change.addChildDefaultEntryMatching?.let { addChildDefaultEntryMatching(scheme, it) }
    }
}
