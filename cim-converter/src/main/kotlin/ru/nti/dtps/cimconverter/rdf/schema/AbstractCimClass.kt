package ru.nti.dtps.cimconverter.rdf.schema

abstract class AbstractCimClass(
    val namespace: RdfNamespace
) {
    val className = this.javaClass.name.split("$").last()
    val fullName = "${namespace.getAlias()}:$className"
}
