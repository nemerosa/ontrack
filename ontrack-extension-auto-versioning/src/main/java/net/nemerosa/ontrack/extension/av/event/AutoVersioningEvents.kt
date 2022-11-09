package net.nemerosa.ontrack.extension.av.event

import net.nemerosa.ontrack.model.events.EventType
import net.nemerosa.ontrack.model.events.SimpleEventType

object AutoVersioningEvents {

    val AUTO_VERSIONING_SUCCESS: EventType = SimpleEventType.of(
        "auto-versioning-success",
        """
                Auto versioning of ${'$'}{PROJECT}/${'$'}{BRANCH} for dependency ${'$'}{X_PROJECT} version "${'$'}{:version}" has been done.
                
                ${'$'}{:message}
                
                Pull request ${'$'}{:pr-name:pr-link}
            """.trimIndent()
    )

    val AUTO_VERSIONING_ERROR: EventType = SimpleEventType.of(
        "auto-versioning-error",
        """
                Auto versioning of ${'$'}{PROJECT}/${'$'}{BRANCH} for dependency ${'$'}{X_PROJECT} version "${'$'}{:version}" has failed.
                
                ${'$'}{:message}
                
                Error: ${'$'}{:error}
            """.trimIndent()
    )

    val AUTO_VERSIONING_PR_MERGE_TIMEOUT_ERROR: EventType = SimpleEventType.of(
        "auto-versioning-pr-merge-timeout-error",
        """
                Auto versioning of ${'$'}{PROJECT}/${'$'}{BRANCH} for dependency ${'$'}{X_PROJECT} version "${'$'}{:version}" has failed.
                
                Timeout while waiting for the PR to be ready to be merged.
                
                Pull request ${'$'}{:pr-name:pr-link}
            """.trimIndent()
    )

}