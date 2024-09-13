package ru.nti.dtps.cimconverter.tocim.substation

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.RawSchemeDto

object SubstationConverter {

    fun convert(scheme: RawSchemeDto): Map<String, RdfResource> {
        return scheme.substations.associate { substation ->
            substation.id to RdfResourceBuilder(substation.id, CimClasses.Substation)
                .addDataProperty(CimClasses.IdentifiedObject.name, substation.name)
                .build()
        }
    }
}
