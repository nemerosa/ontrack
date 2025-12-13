package net.nemerosa.ontrack.boot.support

import jakarta.annotation.PostConstruct
import net.nemerosa.ontrack.common.RunProfile
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

/**
 * Configuration to activate when the `dev` profile is enabled.
 */
@Configuration
@Profile(RunProfile.DEV)
class DevConfig {

    private val logger: Logger = LoggerFactory.getLogger(DevConfig::class.java)

    @PostConstruct
    fun init() {
        logger.warn("Development profile is enabled - not all functionalities are enabled.")
    }

}