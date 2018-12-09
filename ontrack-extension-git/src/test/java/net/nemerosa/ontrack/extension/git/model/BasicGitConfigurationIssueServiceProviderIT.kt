package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.service.GitConfigurationService
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceConfiguration
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceExtension
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BasicGitConfigurationIssueServiceProviderIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var gitConfigurationService: GitConfigurationService

    @Autowired
    private lateinit var basicGitConfigurationIssueServiceProvider: BasicGitConfigurationIssueServiceProvider

    @Test
    fun `No Git configuration`() {
        project {
            val configuredIssueService = basicGitConfigurationIssueServiceProvider.getConfiguredIssueService(this)
            assertNull(configuredIssueService, "No configured issue service when no Git configuration")
        }
    }

    @Test
    fun `Git configuration without any issue service`() {
        project {
            // Create a Git configuration
            val gitConfigurationName = uid("C")
            val gitConfiguration = withDisabledConfigurationTest {
                asUser().with(GlobalSettings::class.java).call {
                    gitConfigurationService.newConfiguration(
                            BasicGitConfiguration.empty()
                                    .withName(gitConfigurationName)
                            // .withIssueServiceConfigurationIdentifier(MockIssueServiceConfiguration.INSTANCE.toIdentifier().format())
                    )
                }
            }
            setProperty(
                    this,
                    GitProjectConfigurationPropertyType::class.java,
                    GitProjectConfigurationProperty(gitConfiguration)
            )
            val configuredIssueService = basicGitConfigurationIssueServiceProvider.getConfiguredIssueService(this)
            assertNull(configuredIssueService, "No configured issue service in the Git configuration")
        }
    }

    @Test
    fun `Git configuration with an issue service`() {
        project {
            // Create a Git configuration
            val gitConfigurationName = uid("C")
            val gitConfiguration = withDisabledConfigurationTest {
                asUser().with(GlobalSettings::class.java).call {
                    gitConfigurationService.newConfiguration(
                            BasicGitConfiguration.empty()
                                    .withName(gitConfigurationName)
                                    .withIssueServiceConfigurationIdentifier(MockIssueServiceConfiguration.INSTANCE.toIdentifier().format())
                    )
                }
            }
            setProperty(
                    this,
                    GitProjectConfigurationPropertyType::class.java,
                    GitProjectConfigurationProperty(gitConfiguration)
            )
            val configuredIssueService = basicGitConfigurationIssueServiceProvider.getConfiguredIssueService(this)
            assertNotNull(configuredIssueService, "Configured issue service in the Git configuration") {
                assertTrue(it.issueServiceExtension is MockIssueServiceExtension)
            }
        }
    }

}