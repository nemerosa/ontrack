package net.nemerosa.ontrack.extension.license

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DurationUnit
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit

@ConfigurationProperties(prefix = LicenseConfigurationProperties.PREFIX)
@Component
class LicenseConfigurationProperties {

    /**
     * License provider
     */
    var provider: String = "none"

    /**
     * Duration before the expiry date, when to emit a warning
     */
    @DurationUnit(ChronoUnit.DAYS)
    var warning: Duration = Duration.ofDays(14)

    companion object {

        const val PREFIX = "ontrack.config.license"

    }

}