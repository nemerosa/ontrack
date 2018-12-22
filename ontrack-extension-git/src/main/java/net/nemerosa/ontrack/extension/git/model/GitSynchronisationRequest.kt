package net.nemerosa.ontrack.extension.git.model

/**
 * Request for a project Git synchronisation.
 */
class GitSynchronisationRequest(
        /**
         * Must the repository be reset?
         */
        val isReset: Boolean
) {
    companion object {

        /**
         * Normal sync request
         */
        val SYNC = GitSynchronisationRequest(false)
    }

}
