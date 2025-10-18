package net.nemerosa.ontrack.extension.av.config

/**
 * List of ways the auto approval is managed.
 */
enum class AutoApprovalMode(
    val displayName: String,
) {

    /**
     * Managed at client level, by Ontrack.
     */
    CLIENT("Managed by Ontrack"),

    /**
     * Delegated to the SCM, for example by using the auto merge feature in GitHub.
     */
    SCM("Managed by SCM");

    companion object {
        /**
         * Default value for the auto approval mode when not set.
         */
        val DEFAULT_AUTO_APPROVAL_MODE = AutoApprovalMode.CLIENT

    }

}