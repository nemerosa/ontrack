package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.support.ApplicationInfo
import net.nemerosa.ontrack.model.support.ApplicationInfoProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class ApplicationInfoServiceImplTest {

    @Test
    fun providers() {
        // Mocking a provider
        val provider = mock(ApplicationInfoProvider::class.java)
        `when`(provider.applicationInfoList).thenReturn(listOf(
                ApplicationInfo.error("Test")
        ))
        // Service
        val service = ApplicationInfoServiceImpl(listOf(provider))
        // Gets the informations
        val infoList = service.applicationInfoList
        assertEquals(1, infoList.size)
        val info = infoList.first()
        assertEquals("Test", info.message)
    }

}