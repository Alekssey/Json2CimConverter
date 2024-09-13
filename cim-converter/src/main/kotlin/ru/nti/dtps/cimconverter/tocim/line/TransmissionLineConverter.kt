package ru.nti.dtps.cimconverter.tocim.line

import ru.nti.dtps.cimconverter.rdf.resource.RdfResource
import ru.nti.dtps.cimconverter.rdf.resource.RdfResourceBuilder
import ru.nti.dtps.cimconverter.rdf.schema.CimClasses
import ru.nti.dtps.dto.scheme.RawSchemeDto

object TransmissionLineConverter {

    fun convert(
        scheme: RawSchemeDto
    ): Map<String, RdfResource> = scheme.transmissionLines.associate { transmissionLine ->
        transmissionLine.id to RdfResourceBuilder(transmissionLine.id, CimClasses.Line)
            .addDataProperty(
                CimClasses.IdentifiedObject.name,
                transmissionLine.name
            ).build()
    }
}
