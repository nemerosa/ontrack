package net.nemerosa.ontrack.model.utils

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CoroutinesUtilsTest {

    @Test
    fun launchWithSecurityContext() {
        val name = uid("user-")

        val authentication = mockk<Authentication>()
        every { authentication.name } returns name

        val securityContext = mockk<SecurityContext>()
        every { securityContext.authentication } returns authentication

        SecurityContextHolder.setContext(securityContext)
        try {
            var registeredName: String? = null

            val job = launchWithSecurityContext {
                registeredName = SecurityContextHolder.getContext().authentication?.name
            }

            runBlocking {
                job.join()
            }

            assertEquals(
                registeredName,
                SecurityContextHolder.getContext().authentication?.name,
                "Context is restored after the call"
            )

            assertEquals(name, registeredName)
        } finally {
            SecurityContextHolder.clearContext()
        }
    }

    @Test
    fun launchAsyncWithSecurityContext() {
        val name = uid("user-")

        val authentication = mockk<Authentication>()
        every { authentication.name } returns name

        val securityContext = mockk<SecurityContext>()
        every { securityContext.authentication } returns authentication

        SecurityContextHolder.setContext(securityContext)
        try {
            var registeredName: String? = null

            val job = launchAsyncWithSecurityContext() {
                registeredName = SecurityContextHolder.getContext().authentication?.name
                true
            }


            runBlocking {
                val result = job.await()
                assertTrue(result, "Result OK")
            }

            assertEquals(
                registeredName,
                SecurityContextHolder.getContext().authentication?.name,
                "Context is restored after the call"
            )

            assertEquals(name, registeredName)
        } finally {
            SecurityContextHolder.clearContext()
        }
    }

}