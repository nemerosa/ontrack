package net.nemerosa.ontrack.extension.stash

import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import net.nemerosa.ontrack.extension.stash.service.StashConfigurationService
import net.nemerosa.ontrack.graphql.AbstractQLKTITJUnit4Support
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.test.TestUtils
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractBitbucketTestSupport : AbstractQLKTITJUnit4Support() {

    @Autowired
    private lateinit var stashConfigurationService: StashConfigurationService

    fun bitbucketConfig(configurationName: String = TestUtils.uid("C")): StashConfiguration =
        withDisabledConfigurationTest {
            asUser().with(GlobalSettings::class.java).call {
                stashConfigurationService.newConfiguration(
                    StashConfiguration(
                        name = configurationName,
                        url = "https://bitbucket.org",
                        user = null,
                        password = null,
                    )
                )
            }
        }

}