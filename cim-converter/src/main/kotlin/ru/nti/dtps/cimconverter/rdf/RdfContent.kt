package ru.nti.dtps.cimconverter.rdf

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource

class RdfContent(
    private val xmlProlog: String,
    private val productSign: String,
    private val rootElementStartTag: String,
    private val resources: Collection<RdfResource>,
    private val rootElementEndTag: String
) {

    fun stringify(): String = mutableListOf<String>().apply {
        add(xmlProlog)
        add(productSign)
        add(rootElementStartTag)
        addAll(resources.map(RdfResource::stringify))
        add(rootElementEndTag)
    }.joinToString(separator = "\n")
}
