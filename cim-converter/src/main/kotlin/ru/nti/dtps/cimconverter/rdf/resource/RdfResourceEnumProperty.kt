package ru.nti.dtps.cimconverter.rdf.resource

import ru.nti.dtps.cimconverter.rdf.schema.CimEnum
import ru.nti.dtps.cimconverter.rdf.schema.CimField

class RdfResourceEnumProperty(
    private val predicate: CimField,
    private val enumValue: CimEnum
) {
    fun stringify(parentIntendLevel: Int): String {
        return "${intends(parentIntendLevel + 1)}<${predicate.fullName} rdf:resource=\"${enumValue.fullName}\"/>"
    }
}
