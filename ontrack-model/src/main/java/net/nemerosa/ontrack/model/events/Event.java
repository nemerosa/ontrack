package net.nemerosa.ontrack.model.events;

import com.google.common.collect.Maps;
import lombok.Data;
import net.nemerosa.ontrack.model.structure.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Definition of an event
 * <p>
 * TODO Icon can be found at GUI level using VALIDATION_STAMP or PROMOTION_LEVEL entities
 * TODO Status can be found at GUI level using VALIDATION_RUN_STATUS values
 */
@Data
public final class Event {

    private static final Pattern EXPRESSION = Pattern.compile("\\$\\{([a-zA-Z_]+)\\}");

    private final String template;
    private final Signature signature;
    private final Map<ProjectEntityType, ProjectEntity> projectEntities;

    public String renderText() {
        return render(PlainEventRenderer.INSTANCE);
    }

    public String render(EventRenderer eventRenderer) {
        Matcher m = EXPRESSION.matcher(template);
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
            // TODO Value
            throw new RuntimeException("NYI");
        } else {
            // Project entity type
            ProjectEntityType projectEntityType = ProjectEntityType.valueOf(expression);
            // Gets the corresponding entity
            ProjectEntity projectEntity = projectEntities.get(projectEntityType);
            if (projectEntity == null) {
                throw new EventMissingEntityException(template, projectEntityType);
            }
            // Rendering
            return eventRenderer.render(projectEntity, this);
        }
    }

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
                    Maps.uniqueIndex(
                            projectEntities,
                            ProjectEntity::getProjectEntityType
                    )
            );
            // Checks the event can be resolved with all its references
            event.renderText();
            // OK
            return event;
        }
    }

    public static Event newBuild(Build build) {
        return Event.of("New build ${BUILD} for branch ${BRANCH} in ${PROJECT}.")
                .withBuild(build)
                .get();
    }

    public static Event newPromotionRun(PromotionRun promotionRun) {
        return Event.of("Build ${BUILD} has been promoted to ${PROMOTION_LEVEL} for branch ${BRANCH} in ${PROJECT}.")
                .withPromotionRun(promotionRun)
                .get();
    }

}
