package net.nemerosa.ontrack.extension.stash.model

import net.nemerosa.ontrack.extension.stash.BitbucketServerFixtures
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StashConfigurationTest {

    val configuration = BitbucketServerFixtures.bitbucketServerConfig()

    @Test
    fun obfuscation() {
        assertEquals("", configuration.obfuscate().password)
    }

    @Test
    fun `Detection of Bitbucket Server SCM engine with wrong URL`() {
        assertFalse(configuration.matchesUrl("https://github.com/nemerosa/yontrack.git"))
    }

    @Test
    fun `Detection of Bitbucket Server SCM engine with matching HTTP URL`() {
        assertFalse(configuration.matchesUrl("https://bitbucket.dev.nemerosa.com/scm/nemerosa/ontrack.git"))
        assertTrue(configuration.matchesUrl("https://bitbucket.dev.yontrack.com/scm/nemerosa/ontrack.git"))
    }

    @Test
    fun `Detection of Bitbucket Server SCM engine with matching SSH URL`() {
        assertTrue(configuration.matchesUrl("ssh://git@bitbucket.dev.yontrack.com:7999/nemerosa/ontrack.git"))
    }
}
