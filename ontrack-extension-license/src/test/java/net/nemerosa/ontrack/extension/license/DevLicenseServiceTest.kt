package net.nemerosa.ontrack.extension.license

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DevLicenseServiceTest {

    private val devLicenseService = DevLicenseService(
        licensedFeatureProviders = listOf(
            object : LicensedFeatureProvider {
                override val providedFeatures: List<ProvidedLicensedFeature> = listOf(
                    LicenseFixtures.sampleProvidedFeature()
                )
            }
        )
    )

    @Test
    fun `Development license is limited to 10 projects`() {
        val license = devLicenseService.license
        assertEquals(10, license.maxProjects, "Development license has a maximum of 10 projects")
    }

    @Test
    fun `Development license has all features being enabled`() {
        val license = devLicenseService.license
        assertTrue(
            license.isFeatureEnabled(LicenseFixtures.sampleFeatureId),
            "Any feature is enabled in the development license"
        )
    }

}