package ru.nti.dtps.cimconverter.rdf.resource

import ru.nti.dtps.cimconverter.rdf.schema.AbstractCimClass
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.cimconverter.rdf.schema.CimEnum
import ru.nti.dtps.cimconverter.rdf.schema.CimField

class RdfResourceBuilder(
    id: String,
    private val type: AbstractCimClass
) {

    val id: RdfResourceId = RdfResourceId(id)
    private val dataProperties = mutableListOf<RdfResourceDataProperty>()
    private val enumProperties = mutableListOf<RdfResourceEnumProperty>()
    private val objectProperties = mutableListOf<RdfResourceObjectProperty>()

    init {
        addDataProperty(CimClasses.IdentifiedObject.mRid, id)
    }

    fun addDataProperty(
        predicate: CimField,
        value: Any
    ): RdfResourceBuilder {
        dataProperties.add(RdfResourceDataProperty(predicate, value.toString()))
        return this
    }

    fun addEnumProperty(
        predicate: CimField,
        enumValue: CimEnum
    ): RdfResourceBuilder {
        enumProperties.add(RdfResourceEnumProperty(predicate, enumValue))
        return this
    }

    fun addObjectProperty(
        predicate: CimField,
        anotherResourceId: RdfResourceId
    ): RdfResourceBuilder {
        objectProperties.add(RdfResourceObjectProperty(predicate, anotherResourceId))
        return this
    }

    fun addObjectProperty(
        predicate: CimField,
        anotherResource: RdfResource
    ): RdfResourceBuilder = addObjectProperty(predicate, anotherResource.id)

    fun build() = RdfResource(
        id = id,
        type = type,
        dataProperties = dataProperties,
        enumProperties = enumProperties,
        objectProperties = objectProperties
    )
}
