package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventType;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.Signature;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface EventRepository {

    void post(Event event);

    List<Event> query(
            List<Integer> allowedProjects,
            int offset,
            int count,
            BiFunction<ProjectEntityType, ID, ProjectEntity> entityLoader,
            Function<String, EventType> eventTypeLoader
    );

    @Deprecated
    List<Event> query(
            List<Integer> allowedProjects,
            ProjectEntityType entityType,
            ID entityId,
            int offset,
            int count,
            BiFunction<ProjectEntityType, ID, ProjectEntity> entityLoader,
            Function<String, EventType> eventTypeLoader
    );

    List<Event> query(
            ProjectEntityType entityType,
            ID entityId,
            int offset,
            int count,
            BiFunction<ProjectEntityType, ID, ProjectEntity> entityLoader,
            Function<String, EventType> eventTypeLoader
    );

    @Deprecated
    List<Event> query(
            List<Integer> allowedProjects,
            EventType eventType,
            ProjectEntityType entityType,
            ID entityId,
            int offset,
            int count,
            BiFunction<ProjectEntityType, ID, ProjectEntity> entityLoader,
            Function<String, EventType> eventTypeLoader
    );

    List<Event> query(
            EventType eventType,
            ProjectEntityType entityType,
            ID entityId,
            int offset,
            int count,
            BiFunction<ProjectEntityType, ID, ProjectEntity> entityLoader,
            Function<String, EventType> eventTypeLoader
    );

    Optional<Signature> getLastEventSignature(ProjectEntityType entityType, ID entityId, EventType eventType);

    Optional<Event> getLastEvent(ProjectEntityType entityType, ID entityId, EventType eventType,
                                 BiFunction<ProjectEntityType, ID, ProjectEntity> entityLoader,
                                 Function<String, EventType> eventTypeLoader);
}
