package net.nemerosa.ontrack.extension.github.ingestion.processing.push

import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Commit
import org.junit.Test
import kotlin.test.assertEquals

class PushPayloadTest {

    @Test
    fun `Checking the paths`() {
        assertEquals(true, payload().isAdded("added-path-in-commit-1"))
        assertEquals(true, payload().isAddedOrModified("added-path-in-commit-1"))

        assertEquals(true, payload().isModified("modified-path-in-commit-1"))
        assertEquals(true, payload().isAddedOrModified("modified-path-in-commit-1"))

        assertEquals(true, payload().isRemoved("removed-path-in-commit-1"))

        assertEquals(true, payload().isAdded("added-path-in-commit-2"))
        assertEquals(true, payload().isAddedOrModified("added-path-in-commit-2"))

        assertEquals(true, payload().isModified("modified-path-in-commit-2"))
        assertEquals(true, payload().isAddedOrModified("modified-path-in-commit-2"))

        assertEquals(true, payload().isRemoved("removed-path-in-commit-2"))
    }

    private fun payload() = PushPayload(
        repository = IngestionHookFixtures.sampleRepository(),
        ref = "refs/heads/${IngestionHookFixtures.sampleBranch}",
        commits = listOf(
            Commit(
                id = "commit-1",
                message = "Commit 1",
                author = IngestionHookFixtures.sampleAuthor(),
                added = listOf(
                    "added-path-in-commit-1",
                ),
                modified = listOf(
                    "modified-path-in-commit-1",
                ),
                removed = listOf(
                    "removed-path-in-commit-1",
                ),
            ),
            Commit(
                id = "commit-2",
                message = "Commit 2",
                author = IngestionHookFixtures.sampleAuthor(),
                added = listOf(
                    "added-path-in-commit-2",
                ),
                modified = listOf(
                    "modified-path-in-commit-2",
                ),
                removed = listOf(
                    "removed-path-in-commit-2",
                ),
            ),
        )
    )

}