package ru.nti.dpts.schememanagerback.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.nti.dtps.cimconverter.CimConverter

@RestController
class Controller {
    @PostMapping("/json/to/cim")
    fun convertCimToScheme(
            @RequestBody schemeDescriptionJson: String
    ): String {
        System.err.println(schemeDescriptionJson)
        val cim: String = CimConverter.toCim(schemeDescriptionJson)
        return cim;
    }
}