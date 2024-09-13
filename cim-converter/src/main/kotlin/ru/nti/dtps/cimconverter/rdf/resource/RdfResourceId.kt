package ru.nti.dtps.cimconverter.rdf.resource

@JvmInline
value class RdfResourceId(private val id: String) {
    override fun toString() = id
}
