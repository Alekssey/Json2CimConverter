package ru.nti.dpts.schememanagerback.scheme.domain

import java.util.*

data class TransmissionLine(
    val id: String,
    val name: String
) {
    companion object {
        fun create(name: String) = TransmissionLine(UUID.randomUUID().toString(), name)
    }
}
