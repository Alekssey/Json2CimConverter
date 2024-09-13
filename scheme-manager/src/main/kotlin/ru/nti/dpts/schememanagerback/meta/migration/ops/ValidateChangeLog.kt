package ru.nti.dpts.schememanagerback.meta.migration.ops

import ru.nti.dpts.schememanagerback.meta.migration.ChangeLog
import ru.nti.dpts.schememanagerback.meta.migration.ops.mutation.CHAINING_KEY_DELIMITER

fun validateAndReturnChangeSets(changeLog: ChangeLog): List<ChangeLog.ChangeSet> {
    return validateChangeSetExistenceAndReturnSets(changeLog)
        .also(::validateChangeSetInfo)
        .also(::validateChanges)
}

private fun validateChangeSetExistenceAndReturnSets(changeLog: ChangeLog): List<ChangeLog.ChangeSet> {
    val changeSets = changeLog.changeLog.filter { it.changeSet != null }.mapNotNull { it.changeSet }
    if (changeSets.isEmpty()) {
        throw IllegalArgumentException("Expected at least one change set")
    }

    return changeSets
}

private fun validateChangeSetInfo(changeSets: List<ChangeLog.ChangeSet>) {
    val ids = mutableSetOf<Int>()
    var previousId = 0
    changeSets.forEach { changeSet ->
        if (changeSet.id <= 0) {
            throw IllegalArgumentException("Change set id must not be positive")
        }
        if (ids.contains(changeSet.id)) {
            throw IllegalArgumentException("Duplicate change set id found: ${changeSet.id}")
        }
        if (changeSet.id <= previousId) {
            throw IllegalArgumentException("Change set id ${changeSet.id} must be higher than previous one")
        }
        previousId = changeSet.id

        if (changeSet.relatedMetaSchemeVersion.isBlank()) {
            throw IllegalArgumentException("Change set related meta scheme version must be specified")
        }
    }
}

private fun validateChanges(changeSets: List<ChangeLog.ChangeSet>) {
    changeSets.forEach { changeSet ->
        if (changeSet.changes.isEmpty()) {
            throw IllegalArgumentException("Change set ${changeSet.id} must contain changes")
        }
        changeSet.changes.forEach { change ->
            change.replaceValue?.let {
                if (it.key.isNullOrBlank() || it.key.split(CHAINING_KEY_DELIMITER).isEmpty()) {
                    throw IllegalArgumentException(
                        "ReplaceValue change of change set ${changeSet.id} must contain scheme document keys joined" +
                            " by \"$CHAINING_KEY_DELIMITER\" delimiter"
                    )
                }

                if (it.type == null) {
                    throw IllegalArgumentException(
                        "ReplaceValue change value type of change set ${changeSet.id} must be specified"
                    )
                }
            }

            change.removeEntry?.let {
                if (it.key.isNullOrBlank() || it.key.split(CHAINING_KEY_DELIMITER).isEmpty()) {
                    throw IllegalArgumentException(
                        "ReplaceValue change of change set ${changeSet.id} must contain scheme document keys joined" +
                            " by \"$CHAINING_KEY_DELIMITER\" delimiter"
                    )
                }
            }
        }
    }
}
