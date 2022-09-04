package net.nemerosa.ontrack.extension.license.fixed

import net.nemerosa.ontrack.common.Time
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
class FixedLicenseService(
    fixedLicenseConfigurationProperties: FixedLicenseConfigurationProperties,
) : LicenseService {

    override var license: License? = License(
        name = fixedLicenseConfigurationProperties.name,
        assignee = fixedLicenseConfigurationProperties.assignee,
        validUntil = Time.fromStorage(fixedLicenseConfigurationProperties.validUntil),
        maxProjects = fixedLicenseConfigurationProperties.maxProjects,
        active = fixedLicenseConfigurationProperties.active,
    )

}