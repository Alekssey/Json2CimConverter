package ru.nti.dpts.schememanagerback.client

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import ru.nti.dpts.schememanagerback.application.properties.ExternalServiceProperties
import ru.nti.dpts.schememanagerback.company.domain.Company
import ru.nti.dpts.schememanagerback.user.domain.User

//@Component
class IntegrationPlatformClient(
    private val externalServiceProperties: ExternalServiceProperties,
    private val client: WebClient
) {

    fun getAllCompanies(): List<Company> = getAllEntities(
        externalServiceProperties.companiesUrl
    )

    fun getAllUsers(): List<User> = getAllEntities(
        externalServiceProperties.usersUrl
    )

    private inline fun <reified T> getAllEntities(uri: String): List<T> {
        val uriSpec = client.get()
        val bodySpec = uriSpec
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)

        return bodySpec.exchangeToFlux { response ->
            if (response.statusCode() == HttpStatus.OK) {
                response.bodyToFlux(T::class.java)
            } else {
                response.createError<T>().flux()
            }
        }.collectList().block() ?: emptyList()
    }
}
