package net.nemerosa.ontrack.extension.license.control

import io.mockk.mockk
import net.nemerosa.ontrack.extension.license.LicenseConfigurationProperties
import net.nemerosa.ontrack.extension.license.LicenseService
import net.nemerosa.ontrack.it.MockSecurityService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.StructureService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled

@Disabled("To be refactored")
class LicenseControlServiceTest {

    private lateinit var licenseConfigurationProperties: LicenseConfigurationProperties
    private lateinit var structureService: StructureService
    private lateinit var securityService: SecurityService
    private lateinit var licenseService: LicenseService
    private lateinit var licenseControlService: LicenseControlService

    @BeforeEach
    fun init() {
        licenseConfigurationProperties = LicenseConfigurationProperties()
        structureService = mockk()
        securityService = MockSecurityService()
        licenseService = mockk()
        licenseControlService = LicenseControlServiceImpl(
            licenseConfigurationProperties = licenseConfigurationProperties,
            structureService = structureService,
            securityService = securityService,
            licenseService = licenseService,
            licensedFeatureProviders = emptyList(),
        )
    }

//    @Test
//    fun `No license in DEV mode, all features enabled`() {
//        every { licenseService.license } returns null
//        every { envService.profiles } returns RunProfile.DEV
//
//        val featureId = uid("f-")
//        assertTrue(
//            licenseControlService.isFeatureEnabled(featureId),
//            "Feature enabled"
//        )
//    }
//
//    @Test
//    fun `License in DEV mode with feature enabled`() {
//        val featureData = LicenseFixtures.sampleFeatureData()
//        every { licenseService.license } returns license(featureData = featureData)
//        every { envService.profiles } returns RunProfile.DEV
//
//        assertTrue(
//            licenseControlService.isFeatureEnabled(featureData.id),
//            "Feature enabled"
//        )
//    }
//
//    @Test
//    fun `License in DEV mode with feature enabled but not active`() {
//        val featureData = LicenseFixtures.sampleFeatureData()
//        every { licenseService.license } returns license(active = false, featureData = featureData)
//        every { envService.profiles } returns RunProfile.DEV
//
//        assertFalse(
//            licenseControlService.isFeatureEnabled(featureData.id),
//            "Feature not enabled"
//        )
//    }
//
//    @Test
//    fun `License in DEV mode with feature not enabled`() {
//
//        val featureId = uid("f-")
//        every { licenseService.license } returns license()
//        every { envService.profiles } returns RunProfile.DEV
//
//        assertFalse(
//            licenseControlService.isFeatureEnabled(featureId),
//            "Feature not enabled"
//        )
//    }
//
//    @Test
//    fun `No license in unit test mode, all features enabled`() {
//
//        every { licenseService.license } returns null
//        every { envService.profiles } returns RunProfile.DEV
//
//        val featureId = uid("f-")
//        assertTrue(
//            licenseControlService.isFeatureEnabled(featureId),
//            "Feature enabled"
//        )
//    }
//
//    @Test
//    fun `No license in production mode, no feature enabled`() {
//
//        every { licenseService.license } returns null
//        every { envService.profiles } returns RunProfile.PROD
//
//        val featureId = uid("f-")
//        assertFalse(
//            licenseControlService.isFeatureEnabled(featureId),
//            "Feature not enabled"
//        )
//    }
//
//    @Test
//    fun `License in production mode with feature enabled`() {
//        val featureData = LicenseFixtures.sampleFeatureData()
//        every { licenseService.license } returns license(featureData = featureData)
//        every { envService.profiles } returns RunProfile.PROD
//
//        assertTrue(
//            licenseControlService.isFeatureEnabled(featureData.id),
//            "Feature enabled"
//        )
//    }
//
//    @Test
//    fun `License in production mode with feature enabled but not active`() {
//        val featureData = LicenseFixtures.sampleFeatureData()
//        every { licenseService.license } returns license(active = false, featureData = featureData)
//        every { envService.profiles } returns RunProfile.PROD
//
//        assertFalse(
//            licenseControlService.isFeatureEnabled(featureData.id),
//            "Feature not enabled"
//        )
//    }
//
//    @Test
//    fun `License in production mode with feature not enabled`() {
//
//        val featureId = uid("f-")
//        every { licenseService.license } returns license()
//        every { envService.profiles } returns RunProfile.PROD
//
//        assertFalse(
//            licenseControlService.isFeatureEnabled(featureId),
//            "Feature not enabled"
//        )
//    }
//
//    private fun license(active: Boolean = true, featureData: LicenseFeatureData? = null) = License(
//        type = "test",
//        name = "Test License",
//        assignee = "Tester",
//        active = active,
//        validUntil = null,
//        maxProjects = 0,
//        features = listOfNotNull(featureData),
//    )

}