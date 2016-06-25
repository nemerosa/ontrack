package net.nemerosa.ontrack.model.events;

import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.Signature;

import java.util.List;
import java.util.Optional;

/**
 * Service used to get access to the list of events.
 */
public interface EventQueryService {

    List<Event> getEvents(int offset, int count);

    List<Event> getEvents(ProjectEntityType entityType, ID entityId, int offset, int count);

    List<Event> getEvents(ProjectEntityType entityType, ID entityId, EventType eventType, int offset, int count);

    Optional<Signature> getLastEventSignature(ProjectEntityType entityType, ID entityId, EventType eventType);

}
