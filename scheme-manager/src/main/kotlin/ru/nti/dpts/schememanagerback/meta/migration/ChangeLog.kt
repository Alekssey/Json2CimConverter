package ru.nti.dpts.schememanagerback.meta.migration

class ChangeLog {
    var changeLog: List<Log> = listOf()

    class Log {
        var changeSet: ChangeSet? = null
    }

    class ChangeSet {
        var id: Int = 0
        var relatedMetaSchemeVersion: String = ""
        var author: String = ""
        var description: String = ""
        var changes: List<Change> = listOf()

        class Change {
            var replaceKey: ReplaceKey? = null
            var replaceValue: ReplaceValue? = null
            var removeEntry: RemoveEntry? = null
            var moveValueToChildNodeField: MoveValueToChildNodeField? = null
            var removeChildEntriesMatching: RemoveChildEntriesMatching? = null
            var addChildDefaultEntryMatching: AddChildDefaultEntryMatching? = null

            class ReplaceKey {
                var key: String = ""
                var replaceWith: String = ""
            }

            class ReplaceValue {
                var key: String = ""
                var findValue: String = ""
                var replaceWith: String = ""
                var type: Type? = null

                val typedFindValue: Any by lazy {
                    convertValueType(findValue, type!!)
                }

                val typedReplaceWithValue: Any by lazy {
                    convertValueType(replaceWith, type!!)
                }

                enum class Type {
                    String
                }
            }

            class RemoveEntry {
                var key: String = ""
            }

            class MoveValueToChildNodeField {
                var sourceKey: String = ""
                var childNodeKey: String = ""
            }

            class RemoveChildEntriesMatching {
                var ifKey: String = ""
                var hasValue: String = ""
                var removeChildEntriesByKeys: List<String> = emptyList()
            }

            class AddChildDefaultEntryMatching {
                var ifKey: String = ""
                var hasValue: String = ""
                var addChildDefaultEntry: AddChildDefaultEntry? = null

                class AddChildDefaultEntry {
                    var key: String = ""
                    var defaultValue: String = ""
                }
            }
        }
    }
}

private fun convertValueType(
    value: String,
    type: ChangeLog.ChangeSet.Change.ReplaceValue.Type
): Any {
    return when (type) {
        ChangeLog.ChangeSet.Change.ReplaceValue.Type.String -> value
    }
}
