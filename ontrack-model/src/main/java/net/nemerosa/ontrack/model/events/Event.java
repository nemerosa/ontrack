package net.nemerosa.ontrack.model.events;

import com.google.common.collect.Maps;
import lombok.Data;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.NameValue;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.nemerosa.ontrack.model.events.PlainEventRenderer.INSTANCE;

/**
 * Definition of an event
 */
@Data
public final class Event {

    private static final Pattern EXPRESSION = Pattern.compile("\\$\\{([:a-zA-Z_]+)\\}");

    private final EventType eventType;
    @Deprecated
    private final String template;
    private final Signature signature;
    private final Map<ProjectEntityType, ProjectEntity> entities;
    private final Map<String, NameValue> values;

    public String renderText() {
        return render(INSTANCE);
    }

    public String render(EventRenderer eventRenderer) {
        Matcher m = EXPRESSION.matcher(eventType.getTemplate());
        StringBuffer output = new StringBuffer();
        while (m.find()) {
            String value = expandExpression(m.group(1), eventRenderer);
            m.appendReplacement(output, value);
        }
        m.appendTail(output);
        return output.toString();
    }

    private String expandExpression(String expression, EventRenderer eventRenderer) {
        if (StringUtils.startsWith(expression, ":")) {
            String valueKey = expression.substring(1);
            NameValue value = values.get(valueKey);
            if (value == null) {
                throw new EventMissingValueException(template, valueKey);
            }
            return eventRenderer.render(valueKey, value, this);
        } else {
            // Project entity type
            ProjectEntityType projectEntityType = ProjectEntityType.valueOf(expression);
            // Gets the corresponding entity
            ProjectEntity projectEntity = entities.get(projectEntityType);
            if (projectEntity == null) {
                throw new EventMissingEntityException(template, projectEntityType);
            }
            // Rendering
            return eventRenderer.render(projectEntity, this);
        }
    }

    @Deprecated
    public static EventBuilder of(String template) {
        return new EventBuilder(null);
    }

    public static EventBuilder of(EventType eventType) {
        return new EventBuilder(eventType);
    }

    public Event withSignature(Signature signature) {
        return new Event(
                eventType,
                template,
                signature,
                entities,
                values
        );
    }

    public static class EventBuilder {

        private final EventType eventType;
        @Deprecated
        private final String template;
        private Signature signature;
        private Collection<ProjectEntity> entities = new ArrayList<>();
        private Map<String, NameValue> values = new LinkedHashMap<>();

        public EventBuilder(EventType eventType) {
            this.eventType = eventType;
            this.template = null;
        }

        public EventBuilder with(Signature signature) {
            this.signature = signature;
            return this;
        }

        public EventBuilder withBuild(Build build) {
            return withBranch(build.getBranch()).with(build).with(build.getSignature());
        }

        public EventBuilder withPromotionRun(PromotionRun promotionRun) {
            return withBuild(promotionRun.getBuild()).with(promotionRun.getPromotionLevel()).with(promotionRun.getSignature());
        }

        public EventBuilder withValidationRun(ValidationRun validationRun) {
            return withBuild(validationRun.getBuild()).with(validationRun.getValidationStamp()).with(validationRun).with(validationRun.getLastStatus().getSignature());
        }

        public EventBuilder withPromotionLevel(PromotionLevel promotionLevel) {
            return withBranch(promotionLevel.getBranch()).with(promotionLevel);
        }

        public EventBuilder withValidationStamp(ValidationStamp validationStamp) {
            return withBranch(validationStamp.getBranch()).with(validationStamp);
        }

        public EventBuilder withBranch(Branch branch) {
            return withProject(branch.getProject()).with(branch);
        }

        public EventBuilder withProject(Project project) {
            return with(project);
        }

        private EventBuilder with(ProjectEntity entity) {
            entities.add(entity);
            return this;
        }

        public EventBuilder withValidationRunStatus(ValidationRunStatusID statusID) {
            return with("status", new NameValue(statusID.getId(), statusID.getName()));
        }

        public EventBuilder with(String name, NameValue value) {
            values.put(name, value);
            return this;
        }

        public EventBuilder with(String name, String value) {
            return with(name, new NameValue(name, value));
        }

        public Event get() {
            // Creates the event
            Event event = new Event(
                    eventType,
                    template,
                    signature,
                    Maps.uniqueIndex(
                            entities,
                            ProjectEntity::getProjectEntityType
                    ),
                    values
            );
            // Checks the event can be resolved with all its references
            event.renderText();
            // OK
            return event;
        }
    }

    @Deprecated
    public static Event newProject(Project project) {
        return Event.of("New project ${PROJECT}.").withProject(project).get();
    }

    @Deprecated
    public static Event updateProject(Project project) {
        return Event.of("Project ${PROJECT} has been updated.").withProject(project).get();
    }

    @Deprecated
    public static Event deleteProject(Project project) {
        return Event.of("Project ${:project} has been deleted.").with("project", project.getName()).get();
    }

    @Deprecated
    public static Event newBranch(Branch branch) {
        return Event.of("New branch ${BRANCH} for project ${PROJECT}.").withBranch(branch).get();
    }

    @Deprecated
    public static Event updateBranch(Branch branch) {
        return Event.of("Branch ${BRANCH} in ${PROJECT} has been updated.").withBranch(branch).get();
    }

    @Deprecated
    public static Event deleteBranch(Branch branch) {
        return Event.of("Branch ${:branch} has been deleted from ${PROJECT}.")
                .withProject(branch.getProject())
                .with("branch", branch.getName())
                .get();
    }

    @Deprecated
    public static Event newPromotionLevel(PromotionLevel promotionLevel) {
        return Event.of("New promotion level ${PROMOTION_LEVEL} for branch ${BRANCH} in ${PROJECT}.")
                .withPromotionLevel(promotionLevel)
                .get();
    }

    @Deprecated
    public static Event newValidationStamp(ValidationStamp validationStamp) {
        return Event.of("New validation stamp ${VALIDATION_STAMP} for branch ${BRANCH} in ${PROJECT}.")
                .withValidationStamp(validationStamp)
                .get();
    }

    @Deprecated
    public static Event newBuild(Build build) {
        return Event.of("New build ${BUILD} for branch ${BRANCH} in ${PROJECT}.")
                .withBuild(build)
                .get();
    }

    @Deprecated
    public static Event newPromotionRun(PromotionRun promotionRun) {
        return Event.of("Build ${BUILD} has been promoted to ${PROMOTION_LEVEL} for branch ${BRANCH} in ${PROJECT}.")
                .withPromotionRun(promotionRun)
                .get();
    }

    @Deprecated
    public static Event newValidationRun(ValidationRun validationRun) {
        return Event.of("Build ${BUILD} has run for ${VALIDATION_STAMP} with status ${:status} in branch ${BRANCH} in ${PROJECT}.")
                .withValidationRun(validationRun)
                .withValidationRunStatus(validationRun.getLastStatus().getStatusID())
                .get();
    }

    @Deprecated
    public static Event newValidationRunStatus(ValidationRun validationRun) {
        return Event.of("Status for ${VALIDATION_STAMP} validation ${VALIDATION_RUN} for build ${BUILD} in branch ${BRANCH} of ${PROJECT} has changed to ${:status}.")
                .withValidationRun(validationRun)
                .withValidationRunStatus(validationRun.getLastStatus().getStatusID())
                .get();
    }

}
