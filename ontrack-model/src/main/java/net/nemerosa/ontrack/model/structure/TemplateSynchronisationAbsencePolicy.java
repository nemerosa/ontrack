package net.nemerosa.ontrack.model.structure;

/**
 * Policy to apply when a branch is configured but no longer available.
 */
public enum TemplateSynchronisationAbsencePolicy {

    /**
     * Disable (default)
     */
    DISABLE,

    /**
     * Deletes the branch
     */
    DELETE

}
