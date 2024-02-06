package net.nemerosa.ontrack.extension.stash.client

import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BitbucketServerCommitTest {

    @Test
    fun `Parsing from JSON`() {
        val json = TestUtils.resourceJson("/client/commit.json")
        val commit = json.parse<BitbucketServerCommit>()
        assertEquals(
            "abcdef0123abcdef4567abcdef8987abcdef6543",
            commit.id
        )
        assertEquals(
            "abcdef0",
            commit.displayId
        )
        assertEquals(
            "More work on feature 1",
            commit.message
        )
        assertEquals(
            1359075920,
            commit.authorTimestamp
        )
        assertEquals(
            1449075830,
            commit.committerTimestamp
        )
        assertEquals(
            "Charlie",
            commit.author?.name
        )
        assertEquals(
            "charlie@example.com",
            commit.author?.emailAddress
        )
        assertEquals(
            "Charlie",
            commit.committer.name
        )
        assertEquals(
            "charlie@example.com",
            commit.committer.emailAddress
        )
        assertEquals(
            listOf(
                BitbucketServerParentRef(
                    id = "abcdef0123abcdef4567abcdef8987abcdef6543"
                )
            ),
            commit.parents
        )
    }

}