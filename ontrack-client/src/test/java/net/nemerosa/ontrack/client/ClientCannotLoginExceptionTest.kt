package net.nemerosa.ontrack.client

import org.junit.Test
import kotlin.test.assertEquals

class ClientCannotLoginExceptionTest {

    @Test
    fun `Exception with objects containing formatting control characters`() {
        val ex = ClientCannotLoginException("Something with %DC special characters")
        assertEquals("Something with %DC special characters", ex.message)
    }

}