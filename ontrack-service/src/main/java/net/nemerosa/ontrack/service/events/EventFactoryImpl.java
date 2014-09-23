package net.nemerosa.ontrack.service.events;

import net.nemerosa.ontrack.model.events.*;
import net.nemerosa.ontrack.model.structure.Project;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EventFactoryImpl implements EventFactory {

    public static final EventType NEW_PROJECT = SimpleEventType.of("new_project", "New project ${PROJECT}.");
    public static final EventType UPDATE_PROJECT = SimpleEventType.of("update_project", "Project ${PROJECT} has been updated.");

    private final Map<String, EventType> types;

    public EventFactoryImpl() {
        types = new ConcurrentHashMap<>();
        register(NEW_PROJECT);
        register(UPDATE_PROJECT);
    }

    private void register(EventType eventType) {
        types.put(eventType.getId(), eventType);
    }

    @Override
    public EventType toEventType(String id) {
        EventType eventType = types.get(id);
        if (eventType != null) {
            return eventType;
        } else {
            throw new EventTypeNotFoundException(id);
        }
    }

    @Override
    public Event newProject(Project project) {
        return Event.of(NEW_PROJECT).withProject(project).get();
    }

    @Override
    public Event updateProject(Project project) {
        return Event.of(UPDATE_PROJECT).withProject(project).get();
    }
}
