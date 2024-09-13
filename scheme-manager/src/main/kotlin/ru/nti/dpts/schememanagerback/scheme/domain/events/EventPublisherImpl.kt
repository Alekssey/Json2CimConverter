package ru.nti.dpts.schememanagerback.scheme.domain.events

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.nti.dpts.schememanagerback.application.common.base.DomainEvent
import ru.nti.dpts.schememanagerback.application.common.events.DomainEventListener
import ru.nti.dpts.schememanagerback.application.common.events.DomainEventPublisher
import kotlin.reflect.KClass

//@Component
class EventPublisherImpl {

//    private val logger = LoggerFactory.getLogger(EventPublisherImpl::class.java)
//    private val listenerMap = HashMap<KClass<*>, MutableList<DomainEventListener<out DomainEvent>>>()
//
//    fun registerListener(
//        listener: DomainEventListener<out DomainEvent>
//    ) {
//        logger.debug("Register event lister: {}", listener.javaClass.simpleName)
//        listenerMap.compute(listener.eventType()) { _, value ->
//            val list = value ?: ArrayList()
//            list.add(listener)
//            list
//        }
//    }
//
//    @Suppress("UNCHECKED_CAST")
//    override fun publish(events: Collection<DomainEvent>) {
//        events.forEach { e ->
//            logger.debug("Processing event: {}", e)
//            listenerMap[e::class]?.let { listeners ->
//                sendEvents(listeners as List<DomainEventListener<in DomainEvent>>, e)
//            }
//        }
//    }
//
//    @Suppress("UNCHECKED_CAST")
//    override fun publish(event: DomainEvent) {
//        logger.debug("Processing event: {}", event)
//        listenerMap[event::class]?.let { listeners ->
//            sendEvents(listeners as List<DomainEventListener<in DomainEvent>>, event)
//        }
//    }
//
//    private fun sendEvents(listeners: List<DomainEventListener<in DomainEvent>>, event: DomainEvent) {
//        listeners.forEach { l -> l.handle(event) }
//    }
}
