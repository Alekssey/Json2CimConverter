package ru.nti.dtps.cimconverter.rdf.resource

import ru.nti.dtps.cimconverter.rdf.schema.CimField

class RdfResourceObjectProperty(
    val predicate: CimField,
    val anotherResourceId: RdfResourceId
) {
    fun stringify(parentIntendLevel: Int): String {
        return "${intends(parentIntendLevel + 1)}<${predicate.fullName} rdf:resource=\"#${anotherResourceId}\"/>"
    }
}
