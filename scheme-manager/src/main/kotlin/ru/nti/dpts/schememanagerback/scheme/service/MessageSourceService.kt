package ru.nti.dpts.schememanagerback.scheme.service

import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import ru.nti.dpts.schememanagerback.application.properties.AppProperties

@Service
class MessageSourceService(
    private val appProperties: AppProperties,
    private val messageSource: MessageSource
) {

    fun getMessage(code: String): String {
        return messageSource.getMessage(code, null, appProperties.getLocale())
    }
}
