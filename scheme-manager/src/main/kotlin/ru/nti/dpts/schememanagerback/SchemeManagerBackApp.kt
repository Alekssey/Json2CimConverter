package ru.nti.dpts.schememanagerback

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
//import ru.nti.dpts.schememanagerback.application.properties.ExternalServiceProperties

@SpringBootApplication
//@EnableConfigurationProperties(ExternalServiceProperties::class)
class SchemeManagerBackApp

fun main(args: Array<String>) {
    runApplication<SchemeManagerBackApp>(*args)
}
