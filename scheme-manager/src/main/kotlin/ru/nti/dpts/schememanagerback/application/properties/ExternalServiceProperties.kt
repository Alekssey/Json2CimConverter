package ru.nti.dpts.schememanagerback.application.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "services.address")
data class ExternalServiceProperties(
    private val integrationPlatform: String
) {
    val companiesUrl = "$integrationPlatform/api/integration/v1/internal/companies"
    val usersUrl = "$integrationPlatform/api/integration/v1/internal/users"
}
