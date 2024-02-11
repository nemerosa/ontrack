package net.nemerosa.ontrack.extension.github.client

import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.Month
import kotlin.test.assertEquals

class GitHubCommitTest {

    @Test
    fun `Parsing from JSON`() {
        val json = TestUtils.resourceJson("/client/commit.json")
        val commit = json.parse<GitHubCommit>()

        assertEquals(
            "215b979b3e1f4c484e3e3d1af4330baf8e95f4c4",
            commit.sha
        )

        assertEquals(
            "https://github.com/nemerosa/ontrack-github-integration-test/commit/215b979b3e1f4c484e3e3d1af4330baf8e95f4c4",
            commit.url
        )

        assertEquals(
            "Damien Coraboeuf",
            commit.commit.author?.name
        )

        assertEquals(
            "damien.coraboeuf@nemerosa.com",
            commit.commit.author?.email
        )

        assertEquals(
            LocalDateTime.of(2021, Month.NOVEMBER, 9, 15, 54, 3),
            commit.commit.author?.date
        )

        assertEquals(
            "nemerosa/ontrack#928 Sample GitHub ingestion configuration file",
            commit.commit.message
        )
    }

}