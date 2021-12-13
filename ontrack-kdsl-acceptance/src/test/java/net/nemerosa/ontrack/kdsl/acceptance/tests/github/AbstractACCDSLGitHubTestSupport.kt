package net.nemerosa.ontrack.kdsl.acceptance.tests.github

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.spec.extension.github.GitHubConfiguration

abstract class AbstractACCDSLGitHubTestSupport : AbstractACCDSLTestSupport() {

    /**
     * Creating a fake GitHub configuration.
     *
     * @param name Name for the configuration
     */
    protected fun fakeGitHubConfiguration(
        name: String = uid("GH"),
    ): GitHubConfiguration = GitHubConfiguration(
        name = name,
        url = null, // github.com by default
        oauth2Token = null, // TODO Use the ACCProperties GitHub properties
    )

}