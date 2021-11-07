package net.nemerosa.ontrack.extension.github.ingestion.ui

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStatus
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.Test
import kotlin.test.assertEquals

class GQLRootQueryGitHubIngestionHookPayloadStatusesIT : AbstractQLKTITSupport() {

    @Test
    fun `Getting the list of statuses`() {
        run(
            """ { gitHubIngestionHookPayloadStatuses } """
        ).let { data ->
            assertEquals(
                IngestionHookPayloadStatus.values().map { it.name }.toSet(),
                data.path("gitHubIngestionHookPayloadStatuses").map { it.asText() }.toSet(),
            )
        }
    }

}