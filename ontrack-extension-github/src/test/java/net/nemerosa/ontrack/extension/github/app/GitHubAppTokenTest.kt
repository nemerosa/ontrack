package net.nemerosa.ontrack.extension.github.app

import net.nemerosa.ontrack.common.Time
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GitHubAppTokenTest {

    @Test
    fun `Valid before the date`() {
        val now = Time.now()
        val token = GitHubAppToken("xxx", validUntil = now.plusMinutes(1))
        assertTrue(token.isValid(), "Token is still valid")
    }

    @Test
    fun `Invalid before the date when invalidated`() {
        val now = Time.now()
        val token = GitHubAppToken("xxx", validUntil = now.plusMinutes(1))
        assertTrue(token.isValid(), "Token is still valid")
        token.invalidate()
        assertFalse(token.isValid(), "Token is no longer valid")
    }

    @Test
    fun `Invalid after the date`() {
        val now = Time.now()
        val token = GitHubAppToken("xxx", validUntil = now.minusMinutes(1))
        assertFalse(token.isValid(), "Token is no longer valid")
    }

}