package net.nemerosa.ontrack.model.events;

import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.Configuration;

/**
 * Factory for events.
 */
public interface EventFactory {

    EventType NEW_PROJECT = SimpleEventType.of("new_project", "New project ${PROJECT}.");
    EventType UPDATE_PROJECT = SimpleEventType.of("update_project", "Project ${PROJECT} has been updated.");
    EventType ENABLE_PROJECT = SimpleEventType.of("enable_project", "Project ${PROJECT} has been enabled.");
    EventType DISABLE_PROJECT = SimpleEventType.of("disable_project", "Project ${PROJECT} has been disabled.");
    EventType DELETE_PROJECT = SimpleEventType.of("delete_project", "Project ${:project} has been deleted.");

    EventType NEW_BRANCH = SimpleEventType.of("new_branch", "New branch ${BRANCH} for project ${PROJECT}.");
    EventType UPDATE_BRANCH = SimpleEventType.of("update_branch", "Branch ${BRANCH} in ${PROJECT} has been updated.");
    EventType ENABLE_BRANCH = SimpleEventType.of("enable_branch", "Branch ${BRANCH} in ${PROJECT} has been enabled.");
    EventType DISABLE_BRANCH = SimpleEventType.of("disable_branch", "Branch ${BRANCH} in ${PROJECT} has been disabled.");
    EventType DELETE_BRANCH = SimpleEventType.of("delete_branch", "Branch ${:branch} has been deleted from ${PROJECT}.");

    EventType NEW_BUILD = SimpleEventType.of("new_build", "New build ${BUILD} for branch ${BRANCH} in ${PROJECT}.");
    EventType UPDATE_BUILD = SimpleEventType.of("update_build", "Build ${BUILD} for branch ${BRANCH} in ${PROJECT} has been updated.");
    EventType DELETE_BUILD = SimpleEventType.of("delete_build", "Build ${:build} for branch ${BRANCH} in ${PROJECT} has been deleted.");

    EventType NEW_PROMOTION_LEVEL = SimpleEventType.of("new_promotion_level", "New promotion level ${PROMOTION_LEVEL} for branch ${BRANCH} in ${PROJECT}.");
    EventType IMAGE_PROMOTION_LEVEL = SimpleEventType.of("image_promotion_level", "Image for promotion level ${PROMOTION_LEVEL} for branch ${BRANCH} in ${PROJECT} has changed.");
    EventType UPDATE_PROMOTION_LEVEL = SimpleEventType.of("update_promotion_level", "Promotion level ${PROMOTION_LEVEL} for branch ${BRANCH} in ${PROJECT} has changed.");
    EventType DELETE_PROMOTION_LEVEL = SimpleEventType.of("delete_promotion_level", "Promotion level ${:promotion_level} for branch ${BRANCH} in ${PROJECT} has been deleted.");
    EventType REORDER_PROMOTION_LEVEL = SimpleEventType.of("reorder_promotion_level", "Promotion levels for branch ${BRANCH} in ${PROJECT} have been reordered.");

    EventType NEW_VALIDATION_STAMP = SimpleEventType.of("new_validation_stamp", "New validation stamp ${VALIDATION_STAMP} for branch ${BRANCH} in ${PROJECT}.");
    EventType IMAGE_VALIDATION_STAMP = SimpleEventType.of("image_validation_stamp", "Image for validation stamp ${VALIDATION_STAMP} for branch ${BRANCH} in ${PROJECT} has changed.");
    EventType UPDATE_VALIDATION_STAMP = SimpleEventType.of("update_validation_stamp", "Validation stamp ${VALIDATION_STAMP} for branch ${BRANCH} in ${PROJECT} has been updated.");
    EventType DELETE_VALIDATION_STAMP = SimpleEventType.of("delete_validation_stamp", "Validation stamp ${:validation_stamp} for branch ${BRANCH} in ${PROJECT} has been deleted.");
    EventType REORDER_VALIDATION_STAMP = SimpleEventType.of("reorder_validation_stamp", "Validation stamps for branch ${BRANCH} in ${PROJECT} have been reordered.");

    EventType NEW_PROMOTION_RUN = SimpleEventType.of("new_promotion_run", "Build ${BUILD} has been promoted to ${PROMOTION_LEVEL} for branch ${BRANCH} in ${PROJECT}.");
    EventType DELETE_PROMOTION_RUN = SimpleEventType.of("delete_promotion_run", "Promotion ${PROMOTION_LEVEL} of build ${BUILD} has been deleted for branch ${BRANCH} in ${PROJECT}.");

    EventType NEW_VALIDATION_RUN = SimpleEventType.of("new_validation_run", "Build ${BUILD} has run for ${VALIDATION_STAMP} with status ${:status} in branch ${BRANCH} in ${PROJECT}.");
    EventType NEW_VALIDATION_RUN_STATUS = SimpleEventType.of("new_validation_run_status", "Status for ${VALIDATION_STAMP} validation ${VALIDATION_RUN} for build ${BUILD} in branch ${BRANCH} of ${PROJECT} has changed to ${:status}.");

    EventType PROPERTY_CHANGE = SimpleEventType.of("property_change", "${:property} property has changed for ${:entity} ${REF}.");
    EventType PROPERTY_DELETE = SimpleEventType.of("property_delete", "${:property} property has been removed from ${:entity} ${REF}.");

    EventType NEW_CONFIGURATION = SimpleEventType.of("new_configuration", "${:configuration} configuration has been created.");
    EventType UPDATE_CONFIGURATION = SimpleEventType.of("update_configuration", "${:configuration} configuration has been updated.");
    EventType DELETE_CONFIGURATION = SimpleEventType.of("delete_configuration", "${:configuration} configuration has been deleted.");

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

    <T> Event propertyChange(ProjectEntity entity, PropertyType<T> propertyType);

    <T> Event propertyDelete(ProjectEntity entity, PropertyType<T> propertyType);

    // Configurations

    <T extends Configuration<T>> Event newConfiguration(T configuration);

    <T extends Configuration<T>> Event updateConfiguration(T configuration);

    <T extends Configuration<T>> Event deleteConfiguration(T configuration);
}
