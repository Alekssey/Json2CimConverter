package ru.nti.dpts.schememanagerback.company.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.nti.dpts.schememanagerback.company.domain.Company
import ru.nti.dpts.schememanagerback.company.domain.adapter.CompanyPersister
import ru.nti.dtps.proto.CompanyEventMsg
import java.util.*

@Service
class CompanyService(
//    private val companyPersister: CompanyPersister
) {

//    private val logger = LoggerFactory.getLogger(javaClass)
//
//    @Transactional
//    fun onCompanyEvent(event: CompanyEventMsg) {
//        when (event.eventType) {
//            CompanyEventMsg.EventType.CREATE, CompanyEventMsg.EventType.UPDATE -> companyPersister.save(event.mapToDomain())
//            CompanyEventMsg.EventType.DELETE -> companyPersister.deleteById(UUID.fromString(event.companyId))
//            CompanyEventMsg.EventType.UNRECOGNIZED -> logger.warn("Unrecognized company event type has been received")
//            null -> throw NullPointerException("Company event type cannot be null")
//        }
//    }
//
//    private fun CompanyEventMsg.mapToDomain() = Company(
//        id = UUID.fromString(this.companyId),
//        name = this.name
//    )
}
