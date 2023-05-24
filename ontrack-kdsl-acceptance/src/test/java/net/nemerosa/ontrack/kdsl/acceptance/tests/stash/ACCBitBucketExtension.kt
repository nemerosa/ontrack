package net.nemerosa.ontrack.kdsl.acceptance.tests.stash

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.spec.configurations.configurations
import net.nemerosa.ontrack.kdsl.spec.extension.github.GitHubConfiguration
import net.nemerosa.ontrack.kdsl.spec.extension.github.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.kdsl.spec.extension.github.gitHub
import net.nemerosa.ontrack.kdsl.spec.extension.github.gitHubConfigurationProperty
import net.nemerosa.ontrack.kdsl.spec.extension.stash.BitbucketServerConfiguration
import net.nemerosa.ontrack.kdsl.spec.extension.stash.BitbucketServerProjectConfigurationProperty
import net.nemerosa.ontrack.kdsl.spec.extension.stash.bitbucketServer
import net.nemerosa.ontrack.kdsl.spec.extension.stash.bitbucketServerConfigurationProperty
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.fail

/**
 * GUI tests about the `stash` extension (Bitbucket).
 */
class ACCBitBucketExtension : AbstractACCDSLTestSupport() {

    /**
     * Make sure that deleting a Bitbucket configuration does not remove other SCM configurations.
     */
    @Test
    fun `Deleting a Bitbucket configuration keeps the GitHub configuration of a project`() {
        // BB configuration
        val bbConfName = uid("bb_")
        ontrack.configurations.bitbucketServer.create(
                BitbucketServerConfiguration(
                        name = bbConfName,
                        url = "https://bitbucket.nemerosa.net",
                )
        )
        // GH configuration
        val ghConfName = uid("gh_")
        ontrack.configurations.gitHub.create(
                GitHubConfiguration(
                        name = ghConfName,
                        url = null,
                        oauth2Token = "ABCDEF"
                )
        )
        // Configures a project with both configurations
        project {
            gitHubConfigurationProperty = GitHubProjectConfigurationProperty(
                    configuration = ghConfName,
                    repository = "sample/test",
            )
            bitbucketServerConfigurationProperty = BitbucketServerProjectConfigurationProperty(
                    configuration = bbConfName,
                    project = "sample",
                    repository = "test"
            )

            // Deletes the BB configuration
            ontrack.configurations.bitbucketServer.delete(bbConfName)

            // Checks the project still has a GH configuration
            assertNotNull(gitHubConfigurationProperty, "GH config is still there")

            // Checks the project BB configuration is gone
            assertNull(bitbucketServerConfigurationProperty, "Bitbucket property is gone")

        }
    }

    /**
     * Make sure that deleting a Bitbucket configuration does not remove other SCM configurations with the same name.
     */
    @Test
    fun `Deleting a Bitbucket configuration keeps the GitHub configuration of a project even when having the same name`() {
        // Unique configuration name
        val confName = uid("cnf_")
        ontrack.configurations.bitbucketServer.create(
                BitbucketServerConfiguration(
                        name = confName,
                        url = "https://bitbucket.nemerosa.net",
                )
        )
        // GH configuration
        ontrack.configurations.gitHub.create(
                GitHubConfiguration(
                        name = confName,
                        url = null,
                        oauth2Token = "ABCDEF"
                )
        )
        // Configures a project with both configurations
        project {
            gitHubConfigurationProperty = GitHubProjectConfigurationProperty(
                    configuration = confName,
                    repository = "sample/test",
            )
            bitbucketServerConfigurationProperty = BitbucketServerProjectConfigurationProperty(
                    configuration = confName,
                    project = "sample",
                    repository = "test"
            )

            // Deletes the BB configuration
            ontrack.configurations.bitbucketServer.delete(confName)

            // Checks the project still has a GH configuration
            assertNotNull(gitHubConfigurationProperty, "GH config is still there")

            // Checks the project BB configuration is gone
            assertNull(bitbucketServerConfigurationProperty, "Bitbucket property is gone")

        }
    }

    /**
     * Regression test for #395
     */
    @Test
    fun `Creation and deletion of a configuration`() {
        val confName = uid("conf_")
        // Creating the configuration
        ontrack.configurations.bitbucketServer.create(
                BitbucketServerConfiguration(
                        name = confName,
                        url = "https://bitbucket.nemerosa.net"
                )
        )
        // Getting the configuration by name
        val conf = ontrack.configurations.bitbucketServer.findByName(confName)
                ?: fail("Could not find Bitbucket server conf")
        assertEquals(confName, conf.name)
        // Deletion
        ontrack.configurations.bitbucketServer.delete(confName)
        // Checks it's deleted
        assertNull(
                ontrack.configurations.bitbucketServer.findByName(confName),
                "Bitbucket server conf has been deleted"
        )
    }

    /**
     * Testing the obfuscation
     */
    @Test
    fun `Obfuscation of configuration password in properties`() {
        val confName = uid("conf_")
        // Creating the configuration
        ontrack.configurations.bitbucketServer.create(
                BitbucketServerConfiguration(
                        name = confName,
                        url = "https://bitbucket.nemerosa.net",
                        user = "user",
                        password = "secret",
                )
        )
        // Gets the configuration back
        val conf = ontrack.configurations.bitbucketServer.findByName(confName)
        assertEquals("user", conf?.user)
        assertEquals("", conf?.password)
    }
}
