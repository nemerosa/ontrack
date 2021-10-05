package net.nemerosa.ontrack.extension.github.ui.graphql

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.json.getTextField
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GQLRootQueryGitHubConfigurationsIT : AbstractGitHubTestSupport() {

    @Test
    fun `Getting the list of configurations`() {
        val config = gitHubConfig()
        run(
            """
            {
                gitHubConfigurations {
                    name
                    url
                    user
                    appId
                    appInstallationAccountName
                }
            }
        """
        ) { data ->
            val node = data["gitHubConfigurations"].find { it.getTextField("name") == config.name }
            assertNotNull(node) {
                assertEquals("https://github.com", it.getTextField("url"))
            }
        }
    }

}