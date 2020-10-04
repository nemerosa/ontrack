package net.nemerosa.ontrack.extension.stale

/**
 * Indicates the stale state of a branch
 */
enum class StaleBranchStatus {

    /**
     * Branch is to be kept
     */
    KEEP,

    /**
     * Branch is to be disabled
     */
    DISABLE,

    /**
     * Branch is to be deleted
     */
    DELETE;

    companion object {

        /**
         * Out of two statuses, takes the less disruptive one.
         */
        fun min(a: StaleBranchStatus?, b: StaleBranchStatus?): StaleBranchStatus? =
                if (a == null) {
                    b
                } else if (b == null) {
                    a
                } else if (a < b) {
                    a
                } else {
                    b
                }

    }

}