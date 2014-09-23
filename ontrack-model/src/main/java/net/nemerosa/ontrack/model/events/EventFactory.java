package net.nemerosa.ontrack.model.events;

import net.nemerosa.ontrack.model.structure.*;

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

    Event deleteBranch(Branch branch);

    Event newBuild(Build build);

    Event updateBuild(Build build);

    Event deleteBuild(Build build);

    Event newPromotionLevel(PromotionLevel promotionLevel);

    Event imagePromotionLevel(PromotionLevel promotionLevel);

    Event updatePromotionLevel(PromotionLevel promotionLevel);

    Event deletePromotionLevel(PromotionLevel promotionLevel);

    Event reorderPromotionLevels(Branch branch);

    Event newPromotionRun(PromotionRun promotionRun);

    Event deletePromotionRun(PromotionRun promotionRun);

    Event newValidationStamp(ValidationStamp validationStamp);

    Event imageValidationStamp(ValidationStamp validationStamp);

    Event updateValidationStamp(ValidationStamp validationStamp);

    Event deleteValidationStamp(ValidationStamp validationStamp);

    Event reorderValidationStamps(Branch branch);

    Event newValidationRun(ValidationRun validationRun);

    Event newValidationRunStatus(ValidationRun validationRun);

    <T> Event propertyChange(ProjectEntity entity, PropertyType<T> propertyType);

    <T> Event propertyDelete(ProjectEntity entity, PropertyType<T> propertyType);
}
