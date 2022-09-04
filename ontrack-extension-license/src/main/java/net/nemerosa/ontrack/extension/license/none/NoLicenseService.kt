package net.nemerosa.ontrack.extension.license.none

import net.nemerosa.ontrack.extension.license.License
import net.nemerosa.ontrack.extension.license.LicenseService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

/**
 * No license.
 */
@Service
@ConditionalOnProperty(
    name = ["ontrack.config.license.provider"],
    havingValue = "none",
    matchIfMissing = true
)
class NoLicenseService : LicenseService {

    /**
     * No license.
     */
    override val license: License? = null
}