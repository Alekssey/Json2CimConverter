package ru.nti.dpts.schememanagerback.application

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class SpringAuthUserInfoProvider : UserIdFromRequest {
    override fun invoke(): String {
        val jwt = SecurityContextHolder.getContext().authentication.principal as Jwt
        return jwt.getClaimAsString("sub")
    }
}
