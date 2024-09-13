package ru.nti.dpts.schememanagerback.builder

import ru.nti.dpts.schememanagerback.scheme.domain.TransmissionLine
import java.util.UUID

data class TransmissionLineBuilder(
    private var id: String = UUID.randomUUID().toString(),
    private var name: String = "Transmission line"
) {
    fun withId(id: String) = apply { this.id = id }
    fun withName(name: String) = apply { this.name = name }
    fun build() = TransmissionLine(
        id,
        name
    )
}
