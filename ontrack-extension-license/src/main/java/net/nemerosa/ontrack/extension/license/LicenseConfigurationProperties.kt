package net.nemerosa.ontrack.extension.license

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DurationUnit
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit

@ConfigurationProperties(prefix = LicenseConfigurationProperties.PREFIX)
@Component
@APIName("License configuration")
class LicenseConfigurationProperties {

    @APIDescription("License key")
    var key: String = ""

    @DurationUnit(ChronoUnit.DAYS)
    @APIDescription("Duration before the expiry date, when to emit a warning (expressed by defaults in days)")
    var warning: Duration = Duration.ofDays(14)

    companion object {

        const val PREFIX = "ontrack.config.license"

    }

}