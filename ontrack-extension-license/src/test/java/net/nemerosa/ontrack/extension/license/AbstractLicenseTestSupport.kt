package net.nemerosa.ontrack.extension.license

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties = [
        "ontrack.config.license.provider=fixed",
    ]
)
abstract class AbstractLicenseTestSupport : AbstractDSLTestSupport() {

    @Autowired
    protected lateinit var licenseService: LicenseService

}