package net.nemerosa.ontrack.service.events;

import net.nemerosa.ontrack.model.events.*;
import net.nemerosa.ontrack.model.structure.*;
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
    public static final EventType NEW_VALIDATION_STAMP = SimpleEventType.of("new_validation_stamp", "New validation stamp ${VALIDATION_STAMP} for branch ${BRANCH} in ${PROJECT}.");
    public static final EventType NEW_PROMOTION_RUN = SimpleEventType.of("new_promotion_run", "Build ${BUILD} has been promoted to ${PROMOTION_LEVEL} for branch ${BRANCH} in ${PROJECT}.");
    public static final EventType DELETE_PROMOTION_RUN = SimpleEventType.of("delete_promotion_run", "Promotion ${PROMOTION_LEVEL} of build ${BUILD} has been deleted for branch ${BRANCH} in ${PROJECT}.");
    public static final EventType NEW_VALIDATION_RUN = SimpleEventType.of("new_validation_run", "Build ${BUILD} has run for ${VALIDATION_STAMP} with status ${:status} in branch ${BRANCH} in ${PROJECT}.");
    public static final EventType NEW_VALIDATION_RUN_STATUS = SimpleEventType.of("new_validation_run_status", "Status for ${VALIDATION_STAMP} validation ${VALIDATION_RUN} for build ${BUILD} in branch ${BRANCH} of ${PROJECT} has changed to ${:status}.");

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
        register(NEW_VALIDATION_STAMP);
        register(NEW_PROMOTION_RUN);
        register(DELETE_PROMOTION_RUN);
        register(NEW_VALIDATION_RUN);
        register(NEW_VALIDATION_RUN_STATUS);
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

    @Override
    public Event newPromotionRun(PromotionRun promotionRun) {
        return Event.of(NEW_PROMOTION_RUN)
                .withPromotionRun(promotionRun)
                .get();
    }

    @Override
    public Event deletePromotionRun(PromotionRun promotionRun) {
        return Event.of(DELETE_PROMOTION_RUN)
                .withBranch(promotionRun.getBuild().getBranch())
                .with(promotionRun.getBuild())
                .with(promotionRun.getPromotionLevel())
                .get();
    }

    @Override
    public Event newValidationStamp(ValidationStamp validationStamp) {
        return Event.of(NEW_VALIDATION_STAMP)
                .withValidationStamp(validationStamp)
                .get();
    }

    @Override
    public Event newValidationRun(ValidationRun validationRun) {
        return Event.of(NEW_VALIDATION_RUN)
                .withValidationRun(validationRun)
                .withValidationRunStatus(validationRun.getLastStatus().getStatusID())
                .get();
    }

    @Override
    public Event newValidationRunStatus(ValidationRun validationRun) {
        return Event.of(NEW_VALIDATION_RUN_STATUS)
                .withValidationRun(validationRun)
                .withValidationRunStatus(validationRun.getLastStatus().getStatusID())
                .get();
    }
}
