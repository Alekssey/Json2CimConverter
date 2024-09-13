package ru.nti.dtps.cimconverter

import ru.nti.dtps.cimconverter.fromcim.FromCimConverter
import ru.nti.dtps.cimconverter.tocim.ToCimConverter
import ru.nti.dtps.dto.scheme.RawSchemeMapper
import java.io.InputStream

object CimConverter {

    fun toCim(schemeJson: String): String = ToCimConverter.toCim(
        RawSchemeMapper.fromJsonString(schemeJson)
    )

    fun fromCim(cimXmlFileInputStream: InputStream): String = FromCimConverter.fromCim(
        cimXmlFileInputStream
    )
}
