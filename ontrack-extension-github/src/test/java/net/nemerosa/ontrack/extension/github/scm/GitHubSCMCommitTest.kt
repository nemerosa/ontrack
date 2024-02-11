package net.nemerosa.ontrack.extension.github.scm

import net.nemerosa.ontrack.extension.github.client.GitHubCommit
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.Month
import kotlin.test.assertEquals

class GitHubSCMCommitTest {

    @Test
    fun `Parsing from JSON`() {
        val json = TestUtils.resourceJson("/client/commit.json")
        val ghCommit = json.parse<GitHubCommit>()
        val commit = GitHubSCMCommit(ghCommit)

        assertEquals(
            "215b979b3e1f4c484e3e3d1af4330baf8e95f4c4",
            commit.id
        )

        assertEquals(
            "215b979",
            commit.shortId
        )

        assertEquals(
            "https://github.com/nemerosa/ontrack-github-integration-test/commit/215b979b3e1f4c484e3e3d1af4330baf8e95f4c4",
            commit.link
        )

        assertEquals(
            "Damien Coraboeuf",
            commit.author
        )

        assertEquals(
            "damien.coraboeuf@nemerosa.com",
            commit.authorEmail
        )

        assertEquals(
            LocalDateTime.of(2021, Month.NOVEMBER, 9, 15, 54, 3),
            commit.timestamp
        )

        assertEquals(
            "nemerosa/ontrack#928 Sample GitHub ingestion configuration file",
            commit.message
        )
    }

}