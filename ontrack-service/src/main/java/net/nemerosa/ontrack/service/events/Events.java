package net.nemerosa.ontrack.service.events;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.structure.Build;

/**
 * Factory for known events.
 */
public final class Events {

    private Events() {
    }

    public static Event newBuild(Build build) {
        return Event.of("New build ${BUILD} for branch ${BRANCH} in ${PROJECT}.")
                .withBuild(build)
                .get();
    }
}
