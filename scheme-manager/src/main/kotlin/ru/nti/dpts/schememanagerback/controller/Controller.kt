package ru.nti.dpts.schememanagerback.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.nti.dpts.schememanagerback.service.Service
import ru.nti.dtps.cimconverter.CimConverter

@RestController
class Controller(
        private val converterService: Service
) {
    @PostMapping("/json/to/cim")
    fun convertCimToScheme(
            @RequestBody schemeDescriptionJson: String
    ): String {
        return converterService.convert(schemeDescriptionJson)
    }
}