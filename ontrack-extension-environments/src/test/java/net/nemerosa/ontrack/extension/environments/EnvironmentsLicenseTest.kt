package net.nemerosa.ontrack.extension.environments

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.environments.EnvironmentsLicensedFeatureProvider.Companion.FEATURE_ENVIRONMENTS
import net.nemerosa.ontrack.extension.license.control.LicenseControlService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class EnvironmentsLicenseTest {

    private lateinit var licenseControlService: LicenseControlService
    private lateinit var environmentsLicense: EnvironmentsLicense

    @BeforeEach
    fun before() {
        licenseControlService = mockk()
        environmentsLicense = EnvironmentsLicense(licenseControlService)
    }

    @Test
    fun `Feature enabled`() {
        every { licenseControlService.isFeatureEnabled(FEATURE_ENVIRONMENTS) } returns true
        assertTrue(
            environmentsLicense.environmentFeatureEnabled
        )
    }

}