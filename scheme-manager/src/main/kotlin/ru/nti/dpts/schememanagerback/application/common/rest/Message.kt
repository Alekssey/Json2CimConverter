package ru.nti.dpts.schememanagerback.application.common.rest

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI

data class BusinessError(val message: String)

typealias Message = String

data class ValidationError(val errorCode: String)

fun restBusinessError(message: String, status: HttpStatus): ResponseEntity<BusinessError> =
    ResponseEntity
        .status(status)
        .body(BusinessError(message))

fun ok(body: Any? = null) = ResponseEntity.ok().body(body)

fun restError(body: Any? = null, status: HttpStatus) = ResponseEntity
    .status(status)
    .body(body)

fun created(location: URI) = ResponseEntity.created(location).build<Any>()

fun noContent() = ResponseEntity.noContent().build<Any>()

fun internalServerError() = ResponseEntity.internalServerError().build<Any>()
