package ru.nti.dpts.schememanagerback.meta.migration.ops.mutation

import org.bson.Document
import ru.nti.dpts.schememanagerback.meta.migration.ChangeLog

fun replaceKey(scheme: Document, replaceKeyChange: ChangeLog.ChangeSet.Change.ReplaceKey) {
    doOnTargetEntries(scheme, replaceKeyChange.key.split(CHAINING_KEY_DELIMITER)) { entry, key ->
        if (entry.containsKey(key)) {
            val value = entry[key]
            entry.remove(key)
            entry.append(replaceKeyChange.replaceWith, value)
        }
    }
}

fun replaceValue(scheme: Document, replaceValueChange: ChangeLog.ChangeSet.Change.ReplaceValue) {
    doOnTargetEntries(scheme, replaceValueChange.key.split(CHAINING_KEY_DELIMITER)) { entry, key ->
        if (entry[key] == replaceValueChange.typedFindValue) {
            entry[key] = replaceValueChange.typedReplaceWithValue
        }
    }
}

fun removeEntry(scheme: Document, removeValueChange: ChangeLog.ChangeSet.Change.RemoveEntry) {
    doOnTargetEntries(scheme, removeValueChange.key.split(CHAINING_KEY_DELIMITER)) { entry, key ->
        entry.remove(key)
    }
}

fun moveValueToChildNodeField(
    scheme: Document,
    moveValueToChildNodeFieldChange: ChangeLog.ChangeSet.Change.MoveValueToChildNodeField
) {
    doOnTargetEntries(
        scheme,
        moveValueToChildNodeFieldChange.sourceKey.split(CHAINING_KEY_DELIMITER)
    ) { parentNode, sourceKey ->
        parentNode[sourceKey]?.let { valueToMove ->
            doOnTargetEntries(
                parentNode,
                moveValueToChildNodeFieldChange.childNodeKey.split(CHAINING_KEY_DELIMITER)
            ) { childNode, childNodeKey ->
                childNode[childNodeKey] = valueToMove
            }
        }
    }
}

fun removeChildEntriesMatching(
    scheme: Document,
    removeChildEntriesMatchingChange: ChangeLog.ChangeSet.Change.RemoveChildEntriesMatching
) {
    doOnTargetEntries(
        scheme,
        removeChildEntriesMatchingChange.ifKey.split(CHAINING_KEY_DELIMITER)
    ) { parentNode, sourceKey ->
        parentNode[sourceKey]?.let { matchingValue ->
            if (removeChildEntriesMatchingChange.hasValue == matchingValue) {
                removeChildEntriesMatchingChange.removeChildEntriesByKeys.forEach { removeChildEntryByKey ->
                    doOnTargetEntries(
                        parentNode,
                        removeChildEntryByKey.split(CHAINING_KEY_DELIMITER)
                    ) { childNode, childNodeKey ->
                        childNode.remove(childNodeKey)
                    }
                }
            }
        }
    }
}

fun addChildDefaultEntryMatching(
    scheme: Document,
    addChildDefaultEntryMatchingChange: ChangeLog.ChangeSet.Change.AddChildDefaultEntryMatching
) {
    doOnTargetEntries(
        scheme,
        addChildDefaultEntryMatchingChange.ifKey.split(CHAINING_KEY_DELIMITER)
    ) { parentNode, sourceKey ->
        parentNode[sourceKey]?.let { matchingValue ->
            if (addChildDefaultEntryMatchingChange.hasValue == matchingValue) {
                addChildDefaultEntryMatchingChange.addChildDefaultEntry?.let { addChildDefaultEntry ->
                    doOnTargetEntries(
                        parentNode,
                        addChildDefaultEntry.key.split(CHAINING_KEY_DELIMITER)
                    ) { childNode, childNodeKey ->
                        childNode.computeIfAbsent(childNodeKey) {
                            addChildDefaultEntry.defaultValue
                        }
                    }
                }
            }
        }
    }
}
