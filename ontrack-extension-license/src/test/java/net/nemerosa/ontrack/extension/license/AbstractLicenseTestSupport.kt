package net.nemerosa.ontrack.extension.license

import net.nemerosa.ontrack.extension.license.fixed.FixedLicenseService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import java.time.LocalDateTime

@TestPropertySource(
    properties = [
        "ontrack.config.license.provider=fixed",
    ]
)
abstract class AbstractLicenseTestSupport : AbstractDSLTestSupport() {

    @Autowired
    protected lateinit var licenseService: FixedLicenseService

    protected fun withLicense(
        validUntil: LocalDateTime? = null,
        maxProjects: Int = 0,
        code: () -> Unit,
    ) {
        val oldLicense = licenseService.license
        try {
            licenseService.license = License(
                name = "Test license",
                assignee = "CI",
                validUntil = validUntil,
                maxProjects = maxProjects,
            )
            code()
        } finally {
            licenseService.license = oldLicense
        }
    }

}