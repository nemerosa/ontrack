package net.nemerosa.ontrack.extension.stash.service

import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNull

class StashConfigurationServiceIT : AbstractServiceTestSupport() {

    @Autowired
    private lateinit var configurationService: StashConfigurationService

    /**
     * Regression test for #395
     */
    @Test
    fun deleteBitbucketOrg() {
        val configurationTest = ontrackConfigProperties.configurationTest
        ontrackConfigProperties.configurationTest = false
        try {
            val confName = "bitbucket.org"
            asUser().with(GlobalSettings::class.java).call<Any> {
                configurationService.newConfiguration(
                    StashConfiguration(
                        confName,
                        "https://bitbucket.org",
                        "",
                        "",
                        null, null,
                    )
                )
                configurationService.deleteConfiguration(confName)
            }
            assertNull(configurationService.findConfiguration(confName))
        } finally {
            ontrackConfigProperties.configurationTest = configurationTest
        }
    }
}