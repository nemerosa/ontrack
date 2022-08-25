package net.nemerosa.ontrack.extension.license.fixed

import net.nemerosa.ontrack.extension.license.License
import net.nemerosa.ontrack.extension.license.LicenseService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    name = ["ontrack.config.license.provider"],
    havingValue = "fixed",
    matchIfMissing = false
)
class FixedLicenseService : LicenseService {

    override var license: License? = null

}