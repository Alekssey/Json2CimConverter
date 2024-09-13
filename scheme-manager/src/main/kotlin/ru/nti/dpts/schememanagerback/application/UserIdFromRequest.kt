package ru.nti.dpts.schememanagerback.application

fun interface UserIdFromRequest {
    operator fun invoke(): String
}
