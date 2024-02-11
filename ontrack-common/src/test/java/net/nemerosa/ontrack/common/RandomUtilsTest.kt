package net.nemerosa.ontrack.common

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class RandomUtilsTest {

    @Test
    fun `Generate a random string with 7 characters`() {
        assertTrue(
            generateRandomString(7).length == 7
        )
    }

}