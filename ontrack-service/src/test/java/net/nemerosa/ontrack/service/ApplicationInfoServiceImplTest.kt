package net.nemerosa.ontrack.service

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ApplicationInfo
import net.nemerosa.ontrack.model.support.ApplicationInfoProvider
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ApplicationInfoServiceImplTest {

    @Test
    fun providers() {
        // Mocking a provider
        val provider = mockk<ApplicationInfoProvider>()
        every { provider.applicationInfoList } returns listOf(
            ApplicationInfo.error("Test")
        )
        // Security mock
        val securityService = mockk<SecurityService>()
        // Service
        val service = ApplicationInfoServiceImpl(listOf(provider), securityService)
        // Gets the informations
        val infoList = service.applicationInfoList
        assertEquals(1, infoList.size)
        val info = infoList.first()
        assertEquals("Test", info.message)
    }

}