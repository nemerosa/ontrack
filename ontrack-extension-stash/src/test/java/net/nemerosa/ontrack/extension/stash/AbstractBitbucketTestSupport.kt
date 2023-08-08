package net.nemerosa.ontrack.extension.stash

import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationProperty
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.stash.service.StashConfigurationService
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.test.TestUtils
import net.nemerosa.ontrack.test.TestUtils.uid
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractBitbucketTestSupport : AbstractQLKTITSupport() {

    @Autowired
    protected lateinit var stashConfigurationService: StashConfigurationService

    fun Project.bitbucketServerConfig() {
        // Creates a BB server configuration
        val config = createBitbucketServerConfig()
        // Sets it to the project
        setProperty(
            this, StashProjectConfigurationPropertyType::class.java, StashProjectConfigurationProperty(
                configuration = config,
                project = bitbucketServerEnv.project,
                repository = bitbucketServerEnv.repository,
                indexationInterval = 0,
                issueServiceConfigurationIdentifier = null,
            )
        )
    }

    private fun createBitbucketServerConfig(): StashConfiguration {
        val configName = uid("bb_")
        val config = StashConfiguration(
            name = configName,
            url = bitbucketServerEnv.url,
            user = bitbucketServerEnv.username,
            password = bitbucketServerEnv.password,
            autoMergeUser = bitbucketServerEnv.autoMergeUser,
            autoMergeToken = bitbucketServerEnv.autoMergeToken,
        )
        asUser().with(GlobalSettings::class.java).call {
            stashConfigurationService.newConfiguration(config)
        }
        return config
    }

    fun bitbucketConfig(configurationName: String = TestUtils.uid("C")): StashConfiguration =
        withDisabledConfigurationTest {
            asUser().with(GlobalSettings::class.java).call {
                stashConfigurationService.newConfiguration(
                    StashConfiguration(
                        name = configurationName,
                        url = "https://bitbucket.org",
                        user = null,
                        password = null,
                        autoMergeUser = null,
                        autoMergeToken = null,
                    )
                )
            }
        }

}