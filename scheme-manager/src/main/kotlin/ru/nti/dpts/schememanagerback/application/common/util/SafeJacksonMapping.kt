package ru.nti.dpts.schememanagerback.application.common.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val objectMapper: ObjectMapper = jacksonObjectMapper()
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)

inline fun <reified T> readValueByJacksonSafely(data: ByteArray?, logError: (message: String?) -> Unit): T? = data?.let {
    try {
        objectMapper.readValue(String(it), T::class.java)
    } catch (e: JsonProcessingException) {
        logError(e.message)
        e.printStackTrace()
        null
    }
}

fun writeValueAsBytesSafely(data: Any?, logError: (message: String?) -> Unit): ByteArray {
    if (data == null) return ByteArray(0)

    return try {
        objectMapper.writeValueAsBytes(data)
    } catch (e: JsonProcessingException) {
        logError(e.message)
        ByteArray(0)
    }
}
