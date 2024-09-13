package ru.nti.dpts.schememanagerback.company.service

import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import ru.nti.dpts.schememanagerback.client.IntegrationPlatformClient
import ru.nti.dpts.schememanagerback.company.domain.adapter.CompanyPersister

//@Service
class CompanyManager(
    private val client: IntegrationPlatformClient,
    private val companyPersister: CompanyPersister
) {

    @EventListener(ApplicationStartedEvent::class)
    fun retrieve() {
        client
            .getAllCompanies()
            .also { companyPersister.saveCompaniesIfDontExist(it) }
    }
}
