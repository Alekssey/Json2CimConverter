package ru.nti.dpts.schememanagerback.scheme.controller

import org.springframework.http.HttpStatus

class ErrorMessage(
    val code: HttpStatus,
    val message: String?
)
