package net.nemerosa.ontrack.service

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ApplicationInfo
import net.nemerosa.ontrack.model.support.ApplicationInfoProvider
import org.junit.Assert.assertEquals
import org.junit.Test

class ApplicationInfoServiceImplTest {

    @Test
    fun providers() {
        // Mocking a provider
        val provider = mock<ApplicationInfoProvider>()
        whenever(provider.applicationInfoList).thenReturn(listOf(
                ApplicationInfo.error("Test")
        ))
        // Security mock
        val securityService = mock<SecurityService>()
        // Service
        val service = ApplicationInfoServiceImpl(listOf(provider), securityService)
        // Gets the informations
        val infoList = service.applicationInfoList
        assertEquals(1, infoList.size)
        val info = infoList.first()
        assertEquals("Test", info.message)
    }

}