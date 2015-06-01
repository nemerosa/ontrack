package net.nemerosa.ontrack.service.events;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventListener;
import net.nemerosa.ontrack.model.events.EventListenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class EventListenerServiceImpl implements EventListenerService {

    private final ApplicationContext context;

    @Autowired
    public EventListenerServiceImpl(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void onEvent(Event event) {
        Collection<EventListener> listeners = context.getBeansOfType(EventListener.class).values();
        listeners.forEach(listener -> listener.onEvent(event));
    }

}
