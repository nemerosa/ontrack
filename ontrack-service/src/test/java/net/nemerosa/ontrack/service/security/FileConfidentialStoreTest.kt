package net.nemerosa.ontrack.service.security

import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.model.support.EnvService
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.assertEquals

internal class FileConfidentialStoreTest {

    @Test
    fun `Directory from the environment by default`() {
        val root = createTempDirectory().toFile()
        val envService = mockk<EnvService>()
        every { envService.getWorkingDir("security", "secrets") } returns root
        val ontrackConfigProperties = OntrackConfigProperties()
        val store = FileConfidentialStore(envService, ontrackConfigProperties)
        assertEquals(root, store.rootDir)
    }

    @Test
    fun `Directory from the configuration properties`() {
        val root = createTempDirectory().toFile()
        val envService = mockk<EnvService>()
        every { envService.getWorkingDir("security", "secrets") } returns root
        val ontrackConfigProperties = OntrackConfigProperties().apply {
            fileKeyStore.directory = File(root, "custom").absolutePath
        }
        val store = FileConfidentialStore(envService, ontrackConfigProperties)
        assertEquals(File(root, "custom"), store.rootDir)
        verify {
            envService wasNot called
        }
    }

}