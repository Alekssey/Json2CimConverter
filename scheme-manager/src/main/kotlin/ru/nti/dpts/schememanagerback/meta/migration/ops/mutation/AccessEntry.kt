package ru.nti.dpts.schememanagerback.meta.migration.ops.mutation

import org.bson.Document

const val CHAINING_KEY_DELIMITER = "."
const val ANY_KEY_KEYWORD = "`anyKey`"

fun doOnTargetEntries(
    entry: Any,
    chainingKey: List<String>,
    doOnTargetEntry: (entry: Document, key: String) -> Unit
) = doOnTargetEntriesRecursively(listOf(entry), chainingKey, chainingKeyIndex = 0, doOnTargetEntry)

private tailrec fun doOnTargetEntriesRecursively(
    entries: Collection<Any>,
    chainingKey: List<String>,
    chainingKeyIndex: Int,
    doOnTargetEntry: (entry: Document, key: String) -> Unit
) {
    if (isNotIndexOutOfBounds(chainingKey, chainingKeyIndex)) {
        val key = chainingKey[chainingKeyIndex]

        if (isItLastElementIndex(chainingKey, chainingKeyIndex)) {
            entries.map { it as Document }.forEach { entry ->
                doOnTargetEntry(entry, key)
            }
        } else {
            val childEntries: Collection<Any> = if (key == ANY_KEY_KEYWORD) {
                entries.flatMap { entry -> (entry as Document).values }
            } else {
                entries.mapNotNull { entry -> (entry as Document)[key] }
            }
            doOnTargetEntriesRecursively(childEntries, chainingKey, chainingKeyIndex + 1, doOnTargetEntry)
        }
    }
}

private fun isNotIndexOutOfBounds(
    chainingKey: List<String>,
    chainingKeyIndex: Int
): Boolean {
    return 0 <= chainingKeyIndex && chainingKeyIndex < chainingKey.size
}

private fun isItLastElementIndex(
    chainingKey: List<String>,
    chainingKeyIndex: Int
): Boolean {
    return chainingKey.size - 1 == chainingKeyIndex
}
