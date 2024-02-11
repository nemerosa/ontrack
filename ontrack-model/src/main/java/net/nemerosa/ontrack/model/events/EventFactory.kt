package net.nemerosa.ontrack.model.events;

import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Factory for events.
 */
public interface EventFactory {

    EventType NEW_PROJECT = SimpleEventType.of("new_project", "New project ${project}.", "When a project is created.");
    EventType UPDATE_PROJECT = SimpleEventType.of("update_project", "Project ${project} has been updated.", "When a project is updated.");
    EventType ENABLE_PROJECT = SimpleEventType.of("enable_project", "Project ${project} has been enabled.", "When a project becomes enabled again.");
    EventType DISABLE_PROJECT = SimpleEventType.of("disable_project", "Project ${project} has been disabled.", "When a project is disabled.");
    EventType DELETE_PROJECT = SimpleEventType.of("delete_project", "Project ${PROJECT} has been deleted.", "When a project is deleted.");

    EventType NEW_BRANCH = SimpleEventType.of("new_branch", "New branch ${branch} for project ${project}.", "When a branch is created.");
    EventType UPDATE_BRANCH = SimpleEventType.of("update_branch", "Branch ${branch} in ${project} has been updated.", "When a branch is updated.");
    EventType ENABLE_BRANCH = SimpleEventType.of("enable_branch", "Branch ${branch} in ${project} has been enabled.", "When a branch becomes enabled again.");
    EventType DISABLE_BRANCH = SimpleEventType.of("disable_branch", "Branch ${branch} in ${project} has been disabled.", "When a branch is disabled.");
    EventType DELETE_BRANCH = SimpleEventType.of("delete_branch", "Branch ${BRANCH} has been deleted from ${project}.", "When a branch is deleted.");

    EventType NEW_BUILD = SimpleEventType.of("new_build", "New build ${build} for branch ${branch} in ${project}.", "When a build is created.");
    EventType UPDATE_BUILD = SimpleEventType.of("update_build", "Build ${build} for branch ${branch} in ${project} has been updated.", "When a build is updated.");
    EventType DELETE_BUILD = SimpleEventType.of("delete_build", "Build ${BUILD} for branch ${branch} in ${project} has been deleted.", "When a build is deleted.");

    EventType NEW_PROMOTION_LEVEL = SimpleEventType.of("new_promotion_level", "New promotion level ${promotionLevel} for branch ${branch} in ${project}.", "When a promotion level is created.");
    EventType IMAGE_PROMOTION_LEVEL = SimpleEventType.of("image_promotion_level", "Image for promotion level ${promotionLevel} for branch ${branch} in ${project} has changed.", "When a promotion level's image is updated.");
    EventType UPDATE_PROMOTION_LEVEL = SimpleEventType.of("update_promotion_level", "Promotion level ${promotionLevel} for branch ${branch} in ${project} has changed.", "When a promotion level is updated.");
    EventType DELETE_PROMOTION_LEVEL = SimpleEventType.of("delete_promotion_level", "Promotion level ${PROMOTION_LEVEL} for branch ${branch} in ${project} has been deleted.", "When a promotion level is deleted.");
    EventType REORDER_PROMOTION_LEVEL = SimpleEventType.of("reorder_promotion_level", "Promotion levels for branch ${branch} in ${project} have been reordered.", "When the promotion levels of a branch are reordered.");

    EventType NEW_VALIDATION_STAMP = SimpleEventType.of("new_validation_stamp", "New validation stamp ${validationStamp} for branch ${branch} in ${project}.", "When a validation stamp is created.");
    EventType IMAGE_VALIDATION_STAMP = SimpleEventType.of("image_validation_stamp", "Image for validation stamp ${validationStamp} for branch ${branch} in ${project} has changed.", "When a validation stamp's image is updated.");
    EventType UPDATE_VALIDATION_STAMP = SimpleEventType.of("update_validation_stamp", "Validation stamp ${validationStamp} for branch ${branch} in ${project} has been updated.", "When a validation stamp is updated.");
    EventType DELETE_VALIDATION_STAMP = SimpleEventType.of("delete_validation_stamp", "Validation stamp ${VALIDATION_STAMP} for branch ${branch} in ${project} has been deleted.", "When a validation stamp is deleted.");
    EventType REORDER_VALIDATION_STAMP = SimpleEventType.of("reorder_validation_stamp", "Validation stamps for branch ${branch} in ${project} have been reordered.", "When the validation stamps of a branch are reordered.");

    EventType NEW_PROMOTION_RUN = SimpleEventType.of("new_promotion_run", "Build ${build} has been promoted to ${promotionLevel} for branch ${branch} in ${project}.", "When a build is promoted.");
    EventType DELETE_PROMOTION_RUN = SimpleEventType.of("delete_promotion_run", "Promotion ${promotionLevel} of build ${build} has been deleted for branch ${branch} in ${project}.", "When the promotion of a build is deleted.");

    EventType NEW_VALIDATION_RUN = SimpleEventType.of("new_validation_run", "Build ${build} has run for the ${validationStamp} with status ${STATUS_NAME} in branch ${branch} in ${project}.", "When a build is validated.");
    EventType NEW_VALIDATION_RUN_STATUS = SimpleEventType.of("new_validation_run_status", "Status for the ${validationStamp} validation ${validationRun} for build ${build} in branch ${branch} of ${project} has changed to ${STATUS_NAME}.", "When the status of the validation of a build is updated.");
    EventType UPDATE_VALIDATION_RUN_STATUS_COMMENT = SimpleEventType.of("update_validation_run_status_comment", "A status message for the ${validationStamp} validation ${validationRun} for build ${build} in branch ${branch} of ${project} has changed.", "When the status message of the validation of a build is updated.");

    EventType PROPERTY_CHANGE = SimpleEventType.of("property_change", "${PROPERTY_NAME} property has changed for ${entity.qualifiedLongName}.", "When a property is edited.");
    EventType PROPERTY_DELETE = SimpleEventType.of("property_delete", "${PROPERTY_NAME} property has been removed from ${entity.qualifiedLongName}.", "When a property is deleted.");

    EventType NEW_CONFIGURATION = SimpleEventType.of("new_configuration", "${CONFIGURATION} configuration has been created.", "When a configuration is created.");
    EventType UPDATE_CONFIGURATION = SimpleEventType.of("update_configuration", "${CONFIGURATION} configuration has been updated.", "When a configuration is updated.");
    EventType DELETE_CONFIGURATION = SimpleEventType.of("delete_configuration", "${CONFIGURATION} configuration has been deleted.", "When a configuration is deleted.");

    /**
     * Gets an event type using its {@linkplain EventType#getId()}  id}.
     */
    EventType toEventType(String id);

    /**
     * Allows a third-party extension to register an additional event type.
     *
     * @param eventType Event type to register.
     */
    void register(EventType eventType);

    // List of known events

    Event newProject(Project project);

    Event updateProject(Project project);

    Event disableProject(Project project);

    Event enableProject(Project project);

    Event deleteProject(Project project);

    Event newBranch(Branch branch);

    Event updateBranch(Branch branch);

    Event disableBranch(Branch branch);

    Event enableBranch(Branch branch);

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

    Event updateValidationRunStatusComment(ValidationRun validationRun);

    <T> Event propertyChange(ProjectEntity entity, PropertyType<T> propertyType);

    <T> Event propertyDelete(ProjectEntity entity, PropertyType<T> propertyType);

    // Configurations

    <T extends Configuration<T>> Event newConfiguration(T configuration);

    <T extends Configuration<T>> Event updateConfiguration(T configuration);

    <T extends Configuration<T>> Event deleteConfiguration(T configuration);

    // Getting the list of all possible event types

    @NotNull
    Collection<EventType> getEventTypes();
}
