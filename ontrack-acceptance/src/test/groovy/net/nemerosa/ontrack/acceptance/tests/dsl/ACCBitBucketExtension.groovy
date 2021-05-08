package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AbstractACCDSL
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.v4.http.OTNotFoundException
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * GUI tests about the `stash` extension (Bitbucket).
 */
@AcceptanceTestSuite
class ACCBitBucketExtension extends AbstractACCDSL {

    /**
     * Make sure that deleting a Bitbucket configuration does not remove other SCM configurations.
     */
    @Test
    void 'Deleting a Bitbucket configuration keeps the GitHub configuration of a project'() {
        // BB configuration
        String bbConfName = uid("B")
        ontrack.config.stash bbConfName, url: "https://bitbucket.org"
        // GH configuration
        String ghConfName = uid("G")
        ontrack.config.gitHub ghConfName, oauth2Token: 'ABCDEF'
        // Configures a project with both configurations
        String projectName = uid("P")
        ontrack.project(projectName) {
            config {
                stash bbConfName, "PRJ", "ontrack"
                gitHub ghConfName, repository: 'nemerosa/ontrack'
            }
        }
        // Deletes the BB configuration
        ontrack.delete("extension/stash/configurations/${bbConfName}")
        // Checks the project still has a GH configuration
        def ghProperty = ontrack.project(projectName).config.gitHub
        assert ghProperty != null: "GitHub property has been kept"
        assert ghProperty.configuration.name == ghConfName
        // Checks the project BB configuration is gone
        def bbProperty = ontrack.project(projectName).config.stash
        assert bbProperty == null: "Bitbucket property is gone"
    }

    /**
     * Make sure that deleting a Bitbucket configuration does not remove other SCM configurations with the same name.
     */
    @Test
    void 'Deleting a Bitbucket configuration keeps the GitHub configuration of a project even when having the same name'() {
        // Unique configuration name
        String confName = uid("B")
        // BB configuration
        ontrack.config.stash confName, url: "https://bitbucket.org"
        // GH configuration
        ontrack.config.gitHub confName, oauth2Token: 'ABCDEF'
        // Configures a project with both configurations
        String projectName = uid("P")
        ontrack.project(projectName) {
            config {
                stash confName, "PRJ", "ontrack"
                gitHub confName, repository: 'nemerosa/ontrack'
            }
        }
        // Deletes the BB configuration
        ontrack.delete("extension/stash/configurations/${confName}")
        // Checks the project still has a GH configuration
        def ghProperty = ontrack.project(projectName).config.gitHub
        assert ghProperty != null: "GitHub property has been kept"
        assert ghProperty.configuration.name == confName
        // Checks the project BB configuration is gone
        def bbProperty = ontrack.project(projectName).config.stash
        assert bbProperty == null: "Bitbucket property is gone"
    }

    /**
     * Regression test for #395
     */
    @Test
    void 'Creation and deletion of a configuration'() {
        String configurationName = uid('C') + '.org'
        // Creating the configuration
        ontrack.config.stash configurationName, url: 'https://bitbucket.org'
        // Getting the configuration by name
        def conf = ontrack.get("extension/stash/configurations/${configurationName}")
        assert conf.name == configurationName
        // Deletion
        ontrack.delete("extension/stash/configurations/${configurationName}")
        // Checks it's deleted
        try {
            ontrack.get("extension/stash/configurations/${configurationName}")
            assert false: 'Configuration should have been deleted'
        } catch (OTNotFoundException ignore) {
        }
    }

    /**
     * Testing the obfuscation
     */
    @Test
    void 'Obfuscation of configuration password in properties'() {
        String configurationName = uid('C') + '.org'
        // Creating the configuration
        ontrack.config.stash configurationName, url: 'https://bitbucket.org', user: 'user', password: 'secret'
        // Creates a project and configures it for Bitbucket
        String name = TestUtils.uid('P')
        ontrack.project(name).config.stash(configurationName, 'PRJ', 'repos')
        // Gets the property
        def stash = ontrack.project(name).config.stash
        assert stash.configuration.name == configurationName
        assert stash.configuration.user == 'user'
        assert stash.configuration.password == ''
    }
}
