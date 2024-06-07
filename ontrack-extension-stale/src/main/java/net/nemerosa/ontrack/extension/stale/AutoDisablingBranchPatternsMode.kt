package net.nemerosa.ontrack.extension.stale

/**
 * What should be done of branches matching a pattern?
 */
enum class AutoDisablingBranchPatternsMode {

    /**
     * Always keeping the matching branch
     */
    KEEP,

    /**
     * Always keeping the N last branches matching this pattern (based on semantic versioning)
     */
    KEEP_LAST,

    /**
     * Disabling the matching branch
     */
    DISABLE,

}