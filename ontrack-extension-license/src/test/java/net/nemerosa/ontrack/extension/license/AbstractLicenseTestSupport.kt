package net.nemerosa.ontrack.extension.license

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractLicenseTestSupport : AbstractDSLTestSupport() {

    @Autowired
    protected lateinit var licenseService: LicenseService

}