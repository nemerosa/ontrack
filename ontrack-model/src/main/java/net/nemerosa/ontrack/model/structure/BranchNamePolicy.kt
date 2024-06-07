package net.nemerosa.ontrack.model.structure

enum class BranchNamePolicy {

    /**
     * Ontrack name only.
     */
    NAME_ONLY,

    /**
     * Display name if available, Ontrack name other.
     *
     * Accessing the display name can have some cost in performances.
     */
    DISPLAY_NAME_OR_NAME,

    /**
     * Display name only (error if not found).
     *
     * Accessing the display name can have some cost in performances.
     */
    DISPLAY_NAME_ONLY,

}