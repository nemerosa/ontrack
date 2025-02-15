package net.nemerosa.ontrack.extension.github.ui.graphql

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class GitHubConfigurationGraphQLIT : AbstractGitHubTestSupport() {

    @Test
    fun `GitHub configuration must not expose its password field`() {
        doTest(
            gitHubConfiguration = gitHubConfiguration(
                username = "test",
                password = "xxxx",
            )
        )
    }

    @Test
    fun `GitHub configuration must not expose its oauth2 field`() {
        doTest(
            gitHubConfiguration = gitHubConfiguration(
                token = "some-secret-token",
            )
        )
    }

    @Test
    fun `GitHub configuration must not expose its app fields`() {
        doTest(
            gitHubConfiguration = gitHubConfiguration(
                appId = "some-app-id",
                appPrivateKey = TestUtils.resourceString("/test-app.pem"),
                appInstallationAccountName = "some-app-installation-account-name",
            )
        )
    }

    private fun doTest(
        gitHubConfiguration: GitHubEngineConfiguration,
    ) {
        asAdmin {
            project {
                this.configureGitHub(
                    gitHubConfiguration = gitHubConfiguration,
                )

                run(
                    """
                        {
                            project(id: $id) {
                                properties(type: "${GitHubProjectConfigurationPropertyType::class.java.name}") {
                                    value
                                }
                            }
                        }
                    """
                ) { data ->
                    val configuration = data.path("project").path("properties")
                        .single().path("value").path("configuration")
                    assertEquals(
                        gitHubConfiguration.name,
                        configuration.path("name").asText()
                    )
                    assertFalse(
                        configuration.has("oauth2Token"),
                        "No oauth2Token field defined"
                    )
                    assertFalse(
                        configuration.has("password"),
                        "No password field defined"
                    )
                    assertFalse(
                        configuration.has("appPrivateKey"),
                        "No appPrivateKey field defined"
                    )
                }
            }
        }
    }

}