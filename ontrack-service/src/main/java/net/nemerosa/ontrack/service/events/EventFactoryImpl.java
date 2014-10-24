package net.nemerosa.ontrack.service.events;

import net.nemerosa.ontrack.model.events.*;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.NameValue;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

@Service
public class EventFactoryImpl implements EventFactory {

    private final Map<String, EventType> types;

    public EventFactoryImpl() {
        types = new ConcurrentHashMap<>();
        register(NEW_PROJECT);
        register(UPDATE_PROJECT);
        register(DELETE_PROJECT);

        register(NEW_BRANCH);
        register(UPDATE_BRANCH);
        register(DELETE_BRANCH);

        register(NEW_BUILD);
        register(UPDATE_BUILD);
        register(DELETE_BUILD);

        register(NEW_PROMOTION_LEVEL);
        register(IMAGE_PROMOTION_LEVEL);
        register(UPDATE_PROMOTION_LEVEL);
        register(DELETE_PROMOTION_LEVEL);
        register(REORDER_PROMOTION_LEVEL);

        register(NEW_VALIDATION_STAMP);
        register(IMAGE_VALIDATION_STAMP);
        register(UPDATE_VALIDATION_STAMP);
        register(DELETE_VALIDATION_STAMP);
        register(REORDER_VALIDATION_STAMP);

        register(NEW_PROMOTION_RUN);
        register(DELETE_PROMOTION_RUN);

        register(NEW_VALIDATION_RUN);
        register(NEW_VALIDATION_RUN_STATUS);

        register(PROPERTY_CHANGE);
        register(PROPERTY_DELETE);

        register(SYNC_CREATED);
        register(SYNC_UPDATED);
        register(SYNC_IGNORED);
        register(SYNC_DISABLED);
        register(SYNC_DELETED);
        register(SYNC_EXISTING_CLASSIC);
        register(SYNC_EXISTING_DEFINITION);
        register(SYNC_EXISTING_INSTANCE_FROM_OTHER);
    }

    private void register(EventType eventType) {
        if (types.containsKey(eventType.getId())) {
            throw new IllegalStateException(format("Event with ID = %s is already registered.", eventType.getId()));
        } else {
            types.put(eventType.getId(), eventType);
        }
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
    public Event updateBuild(Build build) {
        return Event.of(UPDATE_BUILD)
                .withBuild(build)
                .withNoSignature()
                .get();
    }

    @Override
    public Event deleteBuild(Build build) {
        return Event.of(DELETE_BUILD)
                .withBranch(build.getBranch())
                .with("build", build.getName())
                .get();
    }

    @Override
    public Event newPromotionLevel(PromotionLevel promotionLevel) {
        return Event.of(NEW_PROMOTION_LEVEL)
                .withPromotionLevel(promotionLevel)
                .get();
    }

    @Override
    public Event imagePromotionLevel(PromotionLevel promotionLevel) {
        return Event.of(IMAGE_PROMOTION_LEVEL)
                .withPromotionLevel(promotionLevel)
                .get();
    }

    @Override
    public Event updatePromotionLevel(PromotionLevel promotionLevel) {
        return Event.of(UPDATE_PROMOTION_LEVEL)
                .withPromotionLevel(promotionLevel)
                .get();
    }

    @Override
    public Event deletePromotionLevel(PromotionLevel promotionLevel) {
        return Event.of(DELETE_PROMOTION_LEVEL)
                .withBranch(promotionLevel.getBranch())
                .with("promotion_level", promotionLevel.getName())
                .get();
    }

    @Override
    public Event reorderPromotionLevels(Branch branch) {
        return Event.of(REORDER_PROMOTION_LEVEL)
                .withBranch(branch)
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
    public Event imageValidationStamp(ValidationStamp validationStamp) {
        return Event.of(IMAGE_VALIDATION_STAMP)
                .withValidationStamp(validationStamp)
                .get();
    }

    @Override
    public Event updateValidationStamp(ValidationStamp validationStamp) {
        return Event.of(UPDATE_VALIDATION_STAMP)
                .withValidationStamp(validationStamp)
                .get();
    }

    @Override
    public Event deleteValidationStamp(ValidationStamp validationStamp) {
        return Event.of(DELETE_VALIDATION_STAMP)
                .withBranch(validationStamp.getBranch())
                .with("validation_stamp", validationStamp.getName())
                .get();
    }

    @Override
    public Event reorderValidationStamps(Branch branch) {
        return Event.of(REORDER_VALIDATION_STAMP)
                .withBranch(branch)
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

    @Override
    public <T> Event propertyChange(ProjectEntity entity, PropertyType<T> propertyType) {
        return Event.of(PROPERTY_CHANGE)
                .withRef(entity)
                .with("entity", entity.getProjectEntityType().getDisplayName())
                .with("property", new NameValue(
                        propertyType.getTypeName(),
                        propertyType.getName()
                ))
                .get();
    }

    @Override
    public <T> Event propertyDelete(ProjectEntity entity, PropertyType<T> propertyType) {
        return Event.of(PROPERTY_DELETE)
                .withRef(entity)
                .with("entity", entity.getProjectEntityType().getDisplayName())
                .with("property", new NameValue(
                        propertyType.getTypeName(),
                        propertyType.getName()
                ))
                .get();
    }
}
