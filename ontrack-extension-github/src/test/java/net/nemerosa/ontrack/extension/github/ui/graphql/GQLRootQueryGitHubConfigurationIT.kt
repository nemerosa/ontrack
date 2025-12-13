package net.nemerosa.ontrack.extension.github.ui.graphql

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.json.getTextField
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.assertJsonNull
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GQLRootQueryGitHubConfigurationIT : AbstractGitHubTestSupport() {

    @Test
    fun `Getting a GitHub configuration by name`() {
        val config = gitHubConfiguration()
        run(
            """
            {
                gitHubConfiguration(name: "${config.name}") {
                    name
                }
            }
        """
        ) { data ->
            assertEquals(config.name, data["gitHubConfiguration"].getTextField("name"))
        }
    }

    @Test
    fun `Getting a GitHub configuration by name not found`() {
        val name = uid("gh-")
        run(
            """
            {
                gitHubConfiguration(name: "$name") {
                    name
                }
            }
        """
        ) { data ->
            assertJsonNull(data.path("gitHubConfiguration"))
        }
    }

}