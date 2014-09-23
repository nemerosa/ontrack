package net.nemerosa.ontrack.model.events;

import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;

/**
 * Factory for events.
 */
public interface EventFactory {

    /**
     * Gets an event type using its {@linkplain EventType#getId()}  id}.
     */
    EventType toEventType(String id);

    // List of known events

    Event newProject(Project project);

    Event updateProject(Project project);

    Event deleteProject(Project project);

    Event newBranch(Branch branch);

    Event updateBranch(Branch branch);
}
