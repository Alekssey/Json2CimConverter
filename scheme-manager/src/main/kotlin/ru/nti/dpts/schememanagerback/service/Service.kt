package ru.nti.dpts.schememanagerback.service

import org.springframework.stereotype.Service
import ru.nti.dtps.cimconverter.CimConverter

@Service
class Service {

    fun convert(schemeDescriptionJson: String): String {
        return CimConverter.toCim(schemeDescriptionJson)
    }
}