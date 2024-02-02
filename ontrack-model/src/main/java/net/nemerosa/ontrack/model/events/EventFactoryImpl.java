package net.nemerosa.ontrack.model.events;

import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.Configuration;
import net.nemerosa.ontrack.model.support.NameValue;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collection;
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
        register(DISABLE_PROJECT);
        register(ENABLE_PROJECT);
        register(DELETE_PROJECT);

        register(NEW_BRANCH);
        register(UPDATE_BRANCH);
        register(DISABLE_BRANCH);
        register(ENABLE_BRANCH);
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
        register(UPDATE_VALIDATION_RUN_STATUS_COMMENT);

        register(PROPERTY_CHANGE);
        register(PROPERTY_DELETE);

        register(NEW_CONFIGURATION);
        register(UPDATE_CONFIGURATION);
        register(DELETE_CONFIGURATION);

    }

    @Override
    public void register(EventType eventType) {
        if (types.containsKey(eventType.getId())) {
            throw new IllegalStateException(format("Event with ID = %s is already registered.", eventType.getId()));
        } else {
            types.put(eventType.getId(), eventType);
        }
    }

    @Override
    public @NotNull
    Collection<EventType> getEventTypes() {
        return types.values();
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
        return Event.of(NEW_PROJECT).withProject(project).build();
    }

    @Override
    public Event updateProject(Project project) {
        return Event.of(UPDATE_PROJECT).withProject(project).build();
    }

    @Override
    public Event disableProject(Project project) {
        return Event.of(DISABLE_PROJECT).withProject(project).build();
    }

    @Override
    public Event enableProject(Project project) {
        return Event.of(ENABLE_PROJECT).withProject(project).build();
    }

    @Override
    public Event deleteProject(Project project) {
        return Event.of(DELETE_PROJECT)
                .with("PROJECT", project.getName())
                .with("PROJECT_ID", project.getId().toString())
                .build();
    }

    @Override
    public Event newBranch(Branch branch) {
        return Event.of(NEW_BRANCH).withBranch(branch).build();
    }

    @Override
    public Event updateBranch(Branch branch) {
        return Event.of(UPDATE_BRANCH).withBranch(branch).build();
    }

    @Override
    public Event disableBranch(Branch branch) {
        return Event.of(DISABLE_BRANCH).withBranch(branch).build();
    }

    @Override
    public Event enableBranch(Branch branch) {
        return Event.of(ENABLE_BRANCH).withBranch(branch).build();
    }

    @Override
    public Event deleteBranch(Branch branch) {
        return Event.of(DELETE_BRANCH)
                .withProject(branch.getProject())
                .with("BRANCH", branch.getName())
                .with("BRANCH_ID", branch.getId().toString())
                .build();
    }

    @Override
    public Event newBuild(Build build) {
        return Event.of(NEW_BUILD)
                .withBuild(build)
                .build();
    }

    @Override
    public Event updateBuild(Build build) {
        return Event.of(UPDATE_BUILD)
                .withBuild(build)
                .withNoSignature()
                .build();
    }

    @Override
    public Event deleteBuild(Build build) {
        return Event.of(DELETE_BUILD)
                .withBranch(build.getBranch())
                .with("BUILD", build.getName())
                .with("BUILD_ID", build.getId().toString())
                .build();
    }

    @Override
    public Event newPromotionLevel(PromotionLevel promotionLevel) {
        return Event.of(NEW_PROMOTION_LEVEL)
                .withPromotionLevel(promotionLevel)
                .build();
    }

    @Override
    public Event imagePromotionLevel(PromotionLevel promotionLevel) {
        return Event.of(IMAGE_PROMOTION_LEVEL)
                .withPromotionLevel(promotionLevel)
                .build();
    }

    @Override
    public Event updatePromotionLevel(PromotionLevel promotionLevel) {
        return Event.of(UPDATE_PROMOTION_LEVEL)
                .withPromotionLevel(promotionLevel)
                .build();
    }

    @Override
    public Event deletePromotionLevel(PromotionLevel promotionLevel) {
        return Event.of(DELETE_PROMOTION_LEVEL)
                .withBranch(promotionLevel.getBranch())
                .with("PROMOTION_LEVEL", promotionLevel.getName())
                .with("PROMOTION_LEVEL_ID", promotionLevel.getId().toString())
                .build();
    }

    @Override
    public Event reorderPromotionLevels(Branch branch) {
        return Event.of(REORDER_PROMOTION_LEVEL)
                .withBranch(branch)
                .build();
    }

    @Override
    public Event newPromotionRun(PromotionRun promotionRun) {
        return Event.of(NEW_PROMOTION_RUN)
                .withPromotionRun(promotionRun)
                .build();
    }

    @Override
    public Event deletePromotionRun(PromotionRun promotionRun) {
        return Event.of(DELETE_PROMOTION_RUN)
                .withBranch(promotionRun.getBuild().getBranch())
                .with(promotionRun.getBuild())
                .with(promotionRun)
                .with(promotionRun.getPromotionLevel())
                .build();
    }

    @Override
    public Event newValidationStamp(ValidationStamp validationStamp) {
        return Event.of(NEW_VALIDATION_STAMP)
                .withValidationStamp(validationStamp)
                .build();
    }

    @Override
    public Event imageValidationStamp(ValidationStamp validationStamp) {
        return Event.of(IMAGE_VALIDATION_STAMP)
                .withValidationStamp(validationStamp)
                .build();
    }

    @Override
    public Event updateValidationStamp(ValidationStamp validationStamp) {
        return Event.of(UPDATE_VALIDATION_STAMP)
                .withValidationStamp(validationStamp)
                .build();
    }

    @Override
    public Event deleteValidationStamp(ValidationStamp validationStamp) {
        return Event.of(DELETE_VALIDATION_STAMP)
                .withBranch(validationStamp.getBranch())
                .with("VALIDATION_STAMP", validationStamp.getName())
                .with("VALIDATION_STAMP_ID", validationStamp.getId().toString())
                .build();
    }

    @Override
    public Event reorderValidationStamps(Branch branch) {
        return Event.of(REORDER_VALIDATION_STAMP)
                .withBranch(branch)
                .build();
    }

    @Override
    public Event newValidationRun(ValidationRun validationRun) {
        return Event.of(NEW_VALIDATION_RUN)
                .withValidationRun(validationRun)
                .withValidationRunStatus(validationRun.getLastStatus().getStatusID())
                .build();
    }

    @Override
    public Event newValidationRunStatus(ValidationRun validationRun) {
        return Event.of(NEW_VALIDATION_RUN_STATUS)
                .withValidationRun(validationRun)
                .withValidationRunStatus(validationRun.getLastStatus().getStatusID())
                .build();
    }

    @Override
    public Event updateValidationRunStatusComment(ValidationRun validationRun) {
        return Event.of(UPDATE_VALIDATION_RUN_STATUS_COMMENT)
                .withValidationRun(validationRun)
                .build();
    }

    @Override
    public <T> Event propertyChange(ProjectEntity entity, PropertyType<T> propertyType) {
        return Event.of(PROPERTY_CHANGE)
                .withRef(entity)
                .with(
                        "PROPERTY",
                        new NameValue(
                                propertyType.getName(),
                                propertyType.getTypeName()
                        )
                )
                .build();
    }

    @Override
    public <T> Event propertyDelete(ProjectEntity entity, PropertyType<T> propertyType) {
        return Event.of(PROPERTY_DELETE)
                .withRef(entity)
                .with(
                        "PROPERTY",
                        new NameValue(
                                propertyType.getName(),
                                propertyType.getTypeName()
                        )
                )
                .build();
    }

    @Override
    public <T extends Configuration<T>> Event newConfiguration(T configuration) {
        return Event.of(NEW_CONFIGURATION)
                .with("CONFIGURATION", configuration.getName())
                .build();
    }

    @Override
    public <T extends Configuration<T>> Event updateConfiguration(T configuration) {
        return Event.of(UPDATE_CONFIGURATION)
                .with("CONFIGURATION", configuration.getName())
                .build();
    }

    @Override
    public <T extends Configuration<T>> Event deleteConfiguration(T configuration) {
        return Event.of(DELETE_CONFIGURATION)
                .with("CONFIGURATION", configuration.getName())
                .with("CONFIGURATION_TYPE", configuration.getClass().getName())
                .build();
    }
}
