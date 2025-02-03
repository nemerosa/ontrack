package net.nemerosa.ontrack.service.events

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventListenerService
import net.nemerosa.ontrack.model.events.EventPostService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.repository.EventRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EventPostServiceImpl (
    private val securityService: SecurityService,
    private val eventRepository: EventRepository,
    private val eventListenerService: EventListenerService
) : EventPostService {

    override fun post(event: Event) {
        var e = event
        if (e.signature == null) {
            e = e.withSignature(securityService.currentSignature)
        }
        e = eventRepository.post(e)
        // Notification to the listeners
        eventListenerService.onEvent(e)
    }
}
