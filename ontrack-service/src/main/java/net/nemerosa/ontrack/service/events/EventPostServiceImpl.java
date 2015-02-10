package net.nemerosa.ontrack.service.events;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventPostService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventPostServiceImpl implements EventPostService {

    private final SecurityService securityService;
    private final EventRepository eventRepository;

    @Autowired
    public EventPostServiceImpl(SecurityService securityService, EventRepository eventRepository) {
        this.securityService = securityService;
        this.eventRepository = eventRepository;
    }

    @Override
    public void post(Event event) {
        Event e = event;
        if (e.getSignature() == null) {
            e = e.withSignature(securityService.getCurrentSignature());
        }
        eventRepository.post(e);
    }

}
