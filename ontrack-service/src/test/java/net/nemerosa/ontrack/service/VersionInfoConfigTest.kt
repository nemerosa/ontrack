package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.support.VersionInfoConfig
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class VersionInfoConfigTest {
    
    @Test
    fun parseDate_null() {
        assertNotNull(VersionInfoConfig.parseDate(null))
    }

    @Test
    fun parseDate_blank() {
        assertNotNull(VersionInfoConfig.parseDate(""))
    }

    @Test
    fun parseDate_incorrect() {
        assertNotNull(VersionInfoConfig.parseDate("2014-12-01"))
    }

    @Test
    fun parseDate_correct() {
        assertEquals(
            LocalDateTime.of(2014, 7, 13, 8, 34, 30),
            VersionInfoConfig.parseDate("2014-07-13T08:34:30")
        )
    }
}