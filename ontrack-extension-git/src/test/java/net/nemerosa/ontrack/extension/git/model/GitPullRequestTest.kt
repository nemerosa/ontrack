package net.nemerosa.ontrack.extension.git.model

import org.junit.Test
import kotlin.test.assertEquals

class GitPullRequestTest {

    @Test
    fun `Branches are simplified`() {
        val pr = GitPullRequest(
                id = 1,
                key = "PR-1",
                source = "refs/heads/feature/PRJ-123",
                target = "refs/heads/master",
                title = "My PR",
                status = "open",
                url = "https://issues/PR-1"
        )
        assertEquals("feature/PRJ-123", pr.source)
        assertEquals("master", pr.target)
    }

}