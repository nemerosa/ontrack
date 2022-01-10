package net.nemerosa.ontrack.extension.gitlab

import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration
import net.nemerosa.ontrack.extension.gitlab.service.GitLabConfigurationService
import net.nemerosa.ontrack.graphql.AbstractQLKTITJUnit4Support
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.test.TestUtils.uid
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractGitLabTestSupport : AbstractQLKTITJUnit4Support() {

    @Autowired
    protected lateinit var gitConfigurationService: GitLabConfigurationService

    protected fun gitLabConfig(gitConfigurationName: String = uid("C")): GitLabConfiguration {
        return withDisabledConfigurationTest {
            asUser().with(GlobalSettings::class.java).call {
                gitConfigurationService.newConfiguration(
                    GitLabConfiguration(
                        gitConfigurationName,
                        "https://gitlab.com/nemerosa/test",
                        null,
                        null,
                        false
                    )
                )
            }
        }
    }

}