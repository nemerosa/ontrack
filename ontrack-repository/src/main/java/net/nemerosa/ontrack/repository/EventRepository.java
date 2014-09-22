package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

import java.util.List;
import java.util.function.BiFunction;

public interface EventRepository {

    void post(Event event);

    List<Event> query(
            List<Integer> allowedProjects,
            int offset,
            int count,
            BiFunction<ProjectEntityType, ID, ProjectEntity> entityLoader
    );

    List<Event> query(
            List<Integer> allowedProjects,
            ProjectEntityType entityType,
            ID entityId,
            int offset,
            int count,
            BiFunction<ProjectEntityType, ID, ProjectEntity> entityLoader
    );

}
