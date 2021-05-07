package net.nemerosa.ontrack.extension.github.client

import org.junit.Test
import kotlin.test.assertEquals

class DefaultOntrackGitHubClientTest {

    @Test
    fun `API URL for GitHub dot com`() {
        assertEquals(
            "https://api.github.com",
            DefaultOntrackGitHubClient.getApiRoot("https://github.com")
        )
    }

    @Test
    fun `API URL for GitHub enterprise`() {
        assertEquals(
            "https://github.company.com/api/v3",
            DefaultOntrackGitHubClient.getApiRoot("https://github.company.com")
        )
    }

}