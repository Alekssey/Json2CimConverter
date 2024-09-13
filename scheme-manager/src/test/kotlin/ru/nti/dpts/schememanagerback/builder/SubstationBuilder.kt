package ru.nti.dpts.schememanagerback.builder

import ru.nti.dpts.schememanagerback.scheme.domain.Substation
import java.util.UUID

data class SubstationBuilder(
    private var id: String = UUID.randomUUID().toString(),
    private var name: String = "substation 500/220\\10 : ? < > * | \""
) {
    fun withId(id: String) = apply { this.id = id }
    fun withName(name: String) = apply { this.name = name }
    fun build() = Substation(
        id,
        name
    )
}
