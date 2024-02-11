package net.nemerosa.ontrack.extension.av.event

import net.nemerosa.ontrack.model.events.EventType
import net.nemerosa.ontrack.model.events.SimpleEventType

object AutoVersioningEvents {

    val AUTO_VERSIONING_SUCCESS: EventType = SimpleEventType(
        id = "auto-versioning-success",
        template = """
            Auto versioning of ${'$'}{project}/${'$'}{branch} for dependency ${'$'}{xProject} version "${'$'}{VERSION}" has been done.
            
            ${'$'}{MESSAGE}
            
            Pull request ${'$'}{#.link?text=PR_NAME&href=PR_LINK}
        """.trimIndent(),
        description = "When an auto versioning request succeeds with the creation of a PR (merged or not)."
    )

    val AUTO_VERSIONING_ERROR: EventType = SimpleEventType(
        id = "auto-versioning-error",
        template = """
            Auto versioning of ${'$'}{project}/${'$'}{branch} for dependency ${'$'}{xProject} version "${'$'}{VERSION}" has failed.
            
            ${'$'}{MESSAGE}
            
            Error: ${'$'}{ERROR}
        """.trimIndent(),
        description = "When an auto versioning request fails because of a general error."
    )

    val AUTO_VERSIONING_POST_PROCESSING_ERROR: EventType = SimpleEventType(
        id = "auto-versioning-post-processing-error",
        template = """
            Auto versioning post-processing of ${'$'}{project}/${'$'}{branch} for dependency ${'$'}{xProject} version "${'$'}{VERSION}" has failed.

            ${'$'}{#.link?text=MESSAGE&href=LINK}
        """.trimIndent(),
        description = "When an auto versioning request fails because of the post-processing."
    )

    val AUTO_VERSIONING_PR_MERGE_TIMEOUT_ERROR: EventType = SimpleEventType(
        id = "auto-versioning-pr-merge-timeout-error",
        template = """
            Auto versioning of ${'$'}{project}/${'$'}{branch} for dependency ${'$'}{xProject} version "${'$'}{VERSION}" has failed.
            
            Timeout while waiting for the PR to be ready to be merged.
            
            Pull request ${'$'}{#.link?text=PR_NAME&href=PR_LINK}
        """.trimIndent(),
        description = "When an auto versioning request fails because the corresponding PR could not be merged in time."
    )

}