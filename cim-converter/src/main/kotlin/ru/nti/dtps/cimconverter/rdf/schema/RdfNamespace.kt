package ru.nti.dtps.cimconverter.rdf.schema

interface RdfNamespace {
    fun getAlias(): String
    fun getUri(): String
}
