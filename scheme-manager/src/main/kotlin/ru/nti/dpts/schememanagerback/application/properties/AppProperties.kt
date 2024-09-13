package ru.nti.dpts.schememanagerback.application.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.util.*

@Component
@ConfigurationProperties(prefix = "app")
data class AppProperties(val language: String = "ru") {

    fun getLocale(): Locale = Locale.forLanguageTag(language)
}
