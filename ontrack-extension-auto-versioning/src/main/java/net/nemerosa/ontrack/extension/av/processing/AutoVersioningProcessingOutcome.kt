package net.nemerosa.ontrack.extension.av.processing

/**
 * Status of the processing of a auto versioning order.
 *
 * @property message Display message
 */
enum class AutoVersioningProcessingOutcome(
    val message: String,
) {

    /**
     * The PR has been successfully created.
     */
    CREATED("PR created"),

    /**
     * The order was correct, but no PR was created because there was no change in the target version.
     */
    SAME_VERSION("PR not created because no change in version"),

    /**
     * The order was correct, but the process way not be complete because of a timeout.
     */
    TIMEOUT("PR not created because missing configuration"),

    /**
     * The order was correct, but no PR was created because there was some missing configuration (typically
     * missing Git configuration at target project or branch).
     */
    NO_CONFIG("PR not created because missing configuration"),

}