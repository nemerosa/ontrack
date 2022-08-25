package net.nemerosa.ontrack.extension.license

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNull

class NoLicenseServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var licenseService: LicenseService

    @Test
    fun `No license by default`() {
        val license = licenseService.license
        assertNull(license, "No license by default")
    }

}