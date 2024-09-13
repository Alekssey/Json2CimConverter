package ru.nti.dpts.schememanagerback.meta.migration.ops

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import ru.nti.dpts.schememanagerback.meta.migration.ChangeLog

fun loadChangeSets(changeLogContentString: String): List<ChangeLog.ChangeSet> {
    val changeLog: ChangeLog = Yaml(Constructor(ChangeLog::class.java)).load(changeLogContentString)

    return validateAndReturnChangeSets(changeLog)
}

fun loadChangeLogContentFromResource(resourcePath: String): String {
    return Thread.currentThread().contextClassLoader
        .getResourceAsStream(resourcePath).let { inputStream ->
            inputStream.readBytes().toString(Charsets.UTF_8)
        }
}
