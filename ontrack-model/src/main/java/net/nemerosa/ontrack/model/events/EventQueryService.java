package net.nemerosa.ontrack.model.events;

import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

import java.util.List;

/**
 * Service used to get access to the list of events.
 */
public interface EventQueryService {

    List<Event> getEvents(ProjectEntityType entityType, ID entityId, int offset, int count);

}
