package ru.nti.dtps.cimconverter.rdf.resource

import ru.nti.dtps.cimconverter.rdf.schema.CimField

class RdfResourceDataProperty(
    private val predicate: CimField,
    private val value: String
) {
    fun stringify(parentIntendLevel: Int): String {
        return "${intends(parentIntendLevel + 1)}<${predicate.fullName}>$value</${predicate.fullName}>"
    }
}
