package ru.nti.dtps.cimconverter.rdf.schema

class CimField(
    val cimClass: AbstractCimClass,
    val name: String
) {
    val fullName: String = "${cimClass.fullName}.$name"
    val nameWithNamespaceAlias = "${cimClass.namespace.getAlias()}${name.firsCharUpperCase()}"
    private fun String.firsCharUpperCase() = this.replaceFirstChar { char -> char.uppercaseChar() }
}
