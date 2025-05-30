package net.nemerosa.ontrack.extension.license

import net.nemerosa.ontrack.common.RunProfile
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(RunProfile.DEV)
class DevLicenseService : LicenseService {

    override val license: License = License(
        type = "dev",
        name = "Development license",
        assignee = "Development",
        maxProjects = 10,
        active = true,
        validUntil = null,
        features = emptyList(),
    )
}