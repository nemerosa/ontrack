package net.nemerosa.ontrack.extension.stash.scm

import net.nemerosa.ontrack.extension.stash.client.BitbucketServerCommit
import net.nemerosa.ontrack.extension.stash.model.BitbucketRepository
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.Month
import kotlin.test.assertEquals

class BitbucketServerSCMCommitTest {

    @Test
    fun `Parsing from JSON`() {
        val json = TestUtils.resourceJson("/client/commit.json")
        val bbCommit = json.parse<BitbucketServerCommit>()
        val commit = BitbucketServerSCMCommit(
            root = "https://bitbucket.nemerosa.net",
            repo = BitbucketRepository(project = "PRJ", repository = "test"),
            commit = bbCommit,
        )
        assertEquals(
            "abcdef0123abcdef4567abcdef8987abcdef6543",
            commit.id
        )
        assertEquals(
            "abcdef0",
            commit.shortId
        )
        assertEquals(
            "More work on feature 1",
            commit.message
        )
        assertEquals(
            LocalDateTime.of(2013, Month.JANUARY, 25, 1, 5, 20),
            commit.timestamp
        )
        assertEquals(
            "Charlie",
            commit.author
        )
        assertEquals(
            "charlie@example.com",
            commit.authorEmail
        )
    }

}