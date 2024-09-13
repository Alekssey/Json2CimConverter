package ru.nti.dpts.schememanagerback.transmissionLine.persister

import org.springframework.data.mongodb.core.mapping.Document
import ru.nti.dpts.schememanagerback.transmissionLine.domain.Matrices3x3
import ru.nti.dpts.schememanagerback.transmissionLine.domain.Matrices6x6
import java.util.*

const val TRANSMISSION_LINE_TEMPLATE_COLLECTION = "transmission_line_template"

@Document(collection = TRANSMISSION_LINE_TEMPLATE_COLLECTION)
class TransmissionLineTemplateDoc(
    val id: UUID,
    val name: String,
    val frequency: Int,
    val lastModifiedAt: Long,
    val matrices3x3: Matrices3x3?,
    val matrices6x6: Matrices6x6?,
    val companyId: String
)
