package ru.nti.dtps.cimconverter.rdf.resource

import ru.nti.dtps.cimconverter.rdf.schema.AbstractCimClass
import ru.nti.dtps.cimconverter.rdf.schema.CimField

data class RdfResource(
    val id: RdfResourceId,
    private val type: AbstractCimClass,
    private val dataProperties: List<RdfResourceDataProperty>,
    private val enumProperties: List<RdfResourceEnumProperty>,
    private val objectProperties: List<RdfResourceObjectProperty>
) {

    private val intendLevel = 1

    fun getObjectPropertyByPredicateOrNull(predicate: CimField): RdfResourceObjectProperty? {
        return objectProperties.firstOrNull { it.predicate.fullName == predicate.fullName }
    }

    fun stringify(): String {
        val startTag = "${intends(intendLevel)}<${type.fullName} rdf:ID = \"_$id\">"

        val childElements = dataProperties.map { it.stringify(intendLevel) } +
            enumProperties.map { it.stringify(intendLevel) } +
            objectProperties.map { it.stringify(intendLevel) }

        val endTag = "${intends(intendLevel)}</${type.fullName}>"

        return mutableListOf<String>().apply {
            add(startTag)
            addAll(childElements)
            add(endTag)
        }.joinToString(separator = "\n")
    }

    fun copyWithObjectProperty(
        predicate: CimField,
        anotherResource: RdfResource
    ): RdfResource = copy(
        objectProperties = objectProperties + RdfResourceObjectProperty(predicate, anotherResource.id)
    )

    override fun toString() = stringify()
}
