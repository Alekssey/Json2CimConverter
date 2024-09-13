package ru.nti.dpts.schememanagerback.scheme.domain

import java.util.*

data class Substation(
    val id: String,
    val name: String
) {
    companion object {
        fun create(name: String) = Substation(UUID.randomUUID().toString(), name)
    }
}
