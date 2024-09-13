package ru.nti.dpts.schememanagerback.meta.migration

import org.bson.Document
import java.io.File

fun loadChangeSetsContent(): String {
    return File(
        "src/main/resources/meta/scheme/changelog/changelog.yaml"
    ).readBytes().toString(Charsets.UTF_8)
}

fun loadSchemeDoc(path: String): Document {
    return Document.parse(File(path).inputStream().readBytes().toString(Charsets.UTF_8))
}
