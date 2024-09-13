package ru.nti.dpts.schememanagerback.transmissionLine.domain.adapter

import ru.nti.dpts.schememanagerback.transmissionLine.domain.TransmissionLineTemplate
import java.util.*

interface TransmissionLineTemplatePersister {
    fun save(transmissionLineTemplate: TransmissionLineTemplate)
    fun deleteById(id: UUID)
}
