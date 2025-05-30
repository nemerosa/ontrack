package net.nemerosa.ontrack.extension.license.control


import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.license.*
import net.nemerosa.ontrack.it.MockSecurityService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.StructureService
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LicenseControlServiceImplTest {

    private val licenseConfigurationProperties = LicenseConfigurationProperties()
    private val structureService: StructureService = mockk()
    private val securityService: SecurityService = MockSecurityService()
    private val licenseService: LicenseService = mockk()

    private val licensedFeatureProvider: LicensedFeatureProvider = mockk()

    private val service = LicenseControlServiceImpl(
        licenseConfigurationProperties,
        structureService,
        securityService,
        licenseService,
        licensedFeatureProviders = listOf(licensedFeatureProvider),
    )

    private fun testLicense(
        active: Boolean = true,
        validUntil: LocalDateTime? = null,
        maxProjects: Int = 0,
        features: List<LicenseFeatureData> = emptyList(),
    ) = License(
        type = "test",
        active = active,
        name = "Test",
        assignee = "None",
        validUntil = validUntil,
        maxProjects = maxProjects,
        features = features,
        message = null,
    )

    @Test
    fun `control - with valid license and no project count exceeded`() {
        val license = testLicense(active = true, validUntil = LocalDateTime.now().plusDays(30), maxProjects = 10)
        every { structureService.projectList.size } returns 5

        val result = service.control(license)

        assertTrue(result.active)
        assertEquals(LicenseExpiration.OK, result.expiration)
        assertFalse(result.projectCountExceeded)
    }

    @Test
    fun `control - with expired license`() {
        val license = testLicense(active = true, validUntil = LocalDateTime.now().minusDays(1), maxProjects = 10)
        every { structureService.projectList.size } returns 5

        val result = service.control(license)

        assertTrue(result.active)
        assertEquals(LicenseExpiration.EXPIRED, result.expiration)
        assertFalse(result.projectCountExceeded)
    }

    @Test
    fun `control - with project count exceeded`() {
        val license = testLicense(active = true, validUntil = LocalDateTime.now().plusDays(30), maxProjects = 3)
        every { structureService.projectList.size } returns 5

        val result = service.control(license)

        assertTrue(result.active)
        assertEquals(LicenseExpiration.OK, result.expiration)
        assertTrue(result.projectCountExceeded)
    }

    @Test
    fun `getLicensedFeatures - returns sorted features`() {
        val license = testLicense(active = true)
        every { licenseService.license } returns license

        val licensedFeature1 = ProvidedLicensedFeature(id = "id1", name = "Feature A", alwaysEnabled = false)
        val licensedFeature2 = ProvidedLicensedFeature(id = "id2", name = "Feature B", alwaysEnabled = true)

        every { licensedFeatureProvider.providedFeatures } returns listOf(licensedFeature1, licensedFeature2)

        val result = service.getLicensedFeatures(license)

        assertEquals(2, result.size)
        assertEquals("Feature A", result[0].name)
        assertEquals("Feature B", result[1].name)
        assertFalse(result[0].enabled)
        assertTrue(result[1].enabled)
    }

    @Test
    fun `isFeatureEnabled - with active license and enabled feature`() {
        val license = testLicense(
            active = true,
            features = listOf(
                LicenseFeatureData(
                    id = "someFeature",
                    enabled = true,
                    data = emptyList(),
                )
            )
        )
        every { licenseService.license } returns license

        val result = service.isFeatureEnabled("someFeature")

        assertTrue(result)
    }

    @Test
    fun `isFeatureEnabled - with inactive license`() {
        val license = testLicense(
            active = false,
            features = listOf(
                LicenseFeatureData(
                    id = "someFeature",
                    enabled = true,
                    data = emptyList(),
                )
            )
        )
        every { licenseService.license } returns license

        val result = service.isFeatureEnabled("someFeature")

        assertFalse(result)
    }

}