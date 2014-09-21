package net.nemerosa.ontrack.model.events;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Definition of an event
 */
@Data
public final class Event {

    private final String template;
    private final Signature signature;
    private final Collection<ProjectEntity> projectEntities;

    public static EventBuilder of(String template) {
        return new EventBuilder(template);
    }

    public static class EventBuilder {

        private final String template;
        private Signature signature;
        private Collection<ProjectEntity> projectEntities = new ArrayList<>();

        public EventBuilder(String template) {
            this.template = template;
        }

        public EventBuilder withBuild(Build build) {
            return withBranch(build.getBranch()).with(build).with(build.getSignature());
        }

        public EventBuilder with(Signature signature) {
            this.signature = signature;
            return this;
        }

        public EventBuilder withBranch(Branch branch) {
            return withProject(branch.getProject()).with(branch);
        }

        public EventBuilder withProject(Project project) {
            return with(project);
        }

        private EventBuilder with(ProjectEntity entity) {
            projectEntities.add(entity);
            return this;
        }

        public Event get() {
            // Creates the event
            Event event = new Event(
                    template,
                    signature,
                    projectEntities
            );
            // TODO Checks the event can be resolved with all its references
            // OK
            return event;
        }
    }

    public static Event newBuild(Build build) {
        return Event.of("New build ${BUILD} for branch ${BRANCH} in ${PROJECT}.")
                .withBuild(build)
                .get();
    }

}
