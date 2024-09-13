package ru.nti.dpts.schememanagerback.scheme.persister

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.application.common.events.DomainEventPublisher
import ru.nti.dpts.schememanagerback.scheme.domain.ProjectAggregate
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectExtractor
import ru.nti.dpts.schememanagerback.scheme.domain.adapter.ProjectPersister
import ru.nti.dpts.schememanagerback.scheme.domain.exception.ProjectDoesNotExistsException
import java.time.Duration
import kotlin.jvm.optionals.getOrElse

//@Component
class ProjectAggregateMongoAdapter(
    val projectRepository: ProjectRepository,
    private val eventPublisher: DomainEventPublisher
) : ProjectExtractor, ProjectPersister {

    private val CACHE_EXPIRATION: Duration = Duration.ofDays(1)

    private val cache: Cache<String, ProjectAggregate> =
        Caffeine.newBuilder()
            .expireAfterWrite(CACHE_EXPIRATION)
            .build()

    override fun extract(projectId: String): ProjectAggregate {
        return cache.getIfPresent(projectId)
            ?: projectRepository.findById(projectId)
                .getOrElse { throw ProjectDoesNotExistsException(projectId) }
                .convertToDomain()
    }

    override fun persist(projectAggregate: ProjectAggregate) {
        projectRepository.save(projectAggregate.convertToDoc())
            .apply { eventPublisher.publish(projectAggregate.popEvents()) }
        cache.put(projectAggregate.id, projectAggregate)
    }

    override fun delete(projectId: String) {
        projectRepository.deleteById(projectId)
    }
}
