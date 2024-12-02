package net.nemerosa.ontrack.extension.environments

import io.mockk.mockk
import net.nemerosa.ontrack.extension.license.LicenseConfigurationProperties
import net.nemerosa.ontrack.extension.license.control.LicenseControlServiceImpl
import net.nemerosa.ontrack.extension.license.embedded.EmbeddedLicenseConfigurationProperties
import net.nemerosa.ontrack.extension.license.embedded.EmbeddedLicenseService
import net.nemerosa.ontrack.it.MockSecurityService
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EnvironmentsLicenseTest {

    @Test
    fun `Unlimited license`() {
        val service = EmbeddedLicenseService(
            embeddedLicenseConfigurationProperties = EmbeddedLicenseConfigurationProperties().apply {
                // Same key as in SignatureLicenseServiceTest."License OK"s
                key =
                    "eyJkYXRhIjoiZXlKdVlXMWxJam9pV0V3aUxDSmhjM05wWjI1bFpTSTZJazVsYldWeWIzTmhJRXh2WTJGc0lpd2lkbUZzYVdSVmJuUnBiQ0k2Ym5Wc2JDd2liV0Y0VUhKdmFtVmpkSE1pT2pBc0ltWmxZWFIxY21WeklqcGJleUpwWkNJNkltVjRkR1Z1YzJsdmJpNWxiblpwY205dWJXVnVkSE1pTENKbGJtRmliR1ZrSWpwMGNuVmxMQ0prWVhSaElqcGJleUp1WVcxbElqb2liV0Y0Ulc1MmFYSnZibTFsYm5Seklpd2lkbUZzZFdVaU9pSXdJbjFkZlYxOSIsInNpZ25hdHVyZSI6Ik1FVUNJUURiNmd5WkZNbktINUxXdFhTODFtdFlwMzN2STFRSFNmNldTREdGd2UxUnBRSWdOekMwditTd29ad1pVZ01CcFlDeCtmb2g0L0EwSnVhd0hYU2V5UW1sQ3dvPSJ9"
            },
            licenseKeyPath = "/testing/keys/embedded.key"
        )
        val environmentsLicense = EnvironmentsLicense(
            licenseControlService = LicenseControlServiceImpl(
                licenseConfigurationProperties = LicenseConfigurationProperties(),
                structureService = mockk(relaxed = true),
                securityService = MockSecurityService(),
                licenseService = service,
                envService = mockk(relaxed = true),
                licensedFeatureProviders = emptyList(),
            ),
        )

        assertTrue(environmentsLicense.environmentFeatureEnabled, "Environment feature enabled")
        assertEquals(0, environmentsLicense.maxEnvironments, "No limits to the number of environments")
    }

    @Test
    fun `Limited license`() {
        val service = EmbeddedLicenseService(
            embeddedLicenseConfigurationProperties = EmbeddedLicenseConfigurationProperties().apply {
                key =
                    "eyJkYXRhIjoiZXlKdVlXMWxJam9pV0V3aUxDSmhjM05wWjI1bFpTSTZJazVsYldWeWIzTmhJRXh2WTJGc0lpd2lkbUZzYVdSVmJuUnBiQ0k2Ym5Wc2JDd2liV0Y0VUhKdmFtVmpkSE1pT2pBc0ltWmxZWFIxY21WeklqcGJleUpwWkNJNkltVjRkR1Z1YzJsdmJpNWxiblpwY205dWJXVnVkSE1pTENKbGJtRmliR1ZrSWpwMGNuVmxMQ0prWVhSaElqcGJleUp1WVcxbElqb2liV0Y0Ulc1MmFYSnZibTFsYm5Seklpd2lkbUZzZFdVaU9pSXhNQ0o5WFgxZGZRPT0iLCJzaWduYXR1cmUiOiJNRVFDSUIvTUpEK1JSY1NyNGxObit4MmdTbFduYmVHZkhMRGxKdkN6cVdScXZ6S0RBaUErNkVtcTBsY3EzQm1OYWk4TGk2a292K0pmUmM4eDhMUFNWem1pKzM5a3F3PT0ifQ=="
            },
            licenseKeyPath = "/testing/keys/embedded.key"
        )
        val environmentsLicense = EnvironmentsLicense(
            licenseControlService = LicenseControlServiceImpl(
                licenseConfigurationProperties = LicenseConfigurationProperties(),
                structureService = mockk(relaxed = true),
                securityService = MockSecurityService(),
                licenseService = service,
                envService = mockk(relaxed = true),
                licensedFeatureProviders = emptyList(),
            ),
        )

        assertTrue(environmentsLicense.environmentFeatureEnabled, "Environment feature enabled")
        assertEquals(10, environmentsLicense.maxEnvironments, "NNumber of environments is limited to 10")
    }

}