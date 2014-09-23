package net.nemerosa.ontrack.service.events;

import net.nemerosa.ontrack.model.events.*;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.PromotionLevel;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EventFactoryImpl implements EventFactory {

    public static final EventType NEW_PROJECT = SimpleEventType.of("new_project", "New project ${PROJECT}.");
    public static final EventType UPDATE_PROJECT = SimpleEventType.of("update_project", "Project ${PROJECT} has been updated.");
    public static final EventType DELETE_PROJECT = SimpleEventType.of("delete_project", "Project ${:project} has been deleted.");
    public static final EventType NEW_BRANCH = SimpleEventType.of("new_branch", "New branch ${BRANCH} for project ${PROJECT}.");
    public static final EventType UPDATE_BRANCH = SimpleEventType.of("update_branch", "Branch ${BRANCH} in ${PROJECT} has been updated.");
    public static final EventType DELETE_BRANCH = SimpleEventType.of("delete_branch", "Branch ${:branch} has been deleted from ${PROJECT}.");
    public static final EventType NEW_BUILD = SimpleEventType.of("new_build", "New build ${BUILD} for branch ${BRANCH} in ${PROJECT}.");
    public static final EventType NEW_PROMOTION_LEVEL = SimpleEventType.of("new_promotion_level", "New promotion level ${PROMOTION_LEVEL} for branch ${BRANCH} in ${PROJECT}.");

    private final Map<String, EventType> types;

    public EventFactoryImpl() {
        types = new ConcurrentHashMap<>();
        register(NEW_PROJECT);
        register(UPDATE_PROJECT);
        register(DELETE_PROJECT);
        register(NEW_BRANCH);
        register(UPDATE_BRANCH);
        register(NEW_BUILD);
        register(NEW_PROMOTION_LEVEL);
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

    @Override
    public Event deleteProject(Project project) {
        return Event.of(DELETE_PROJECT).with("project", project.getName()).get();
    }

    @Override
    public Event newBranch(Branch branch) {
        return Event.of(NEW_BRANCH).withBranch(branch).get();
    }

    @Override
    public Event updateBranch(Branch branch) {
        return Event.of(UPDATE_BRANCH).withBranch(branch).get();
    }

    @Override
    public Event deleteBranch(Branch branch) {
        return Event.of(DELETE_BRANCH)
                .withProject(branch.getProject())
                .with("branch", branch.getName())
                .get();
    }

    @Override
    public Event newBuild(Build build) {
        return Event.of(NEW_BUILD)
                .withBuild(build)
                .get();
    }

    @Override
    public Event newPromotionLevel(PromotionLevel promotionLevel) {
        return Event.of(NEW_PROMOTION_LEVEL)
                .withPromotionLevel(promotionLevel)
                .get();
    }
}
