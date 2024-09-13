package ru.nti.dtps.cimconverter.rdf.schema

class CimEnum(
    val cimClass: AbstractCimClass,
    val name: String
) {
    val fullName: String = "${cimClass.namespace.getUri()}${cimClass.className}.$name"
}
