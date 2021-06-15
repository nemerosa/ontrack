package net.nemerosa.ontrack.extension.bitbucket.cloud.client

import org.junit.Test
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

class DefaultBitbucketCloudClientTest {

    @Test
    fun `Date time parsing`() {
        val input = "2021-06-10T13:55:21.161272+00:00"
        val ldt = LocalDateTime.parse(input, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        assertEquals(2021, ldt.year)
        assertEquals(Month.JUNE, ldt.month)
        assertEquals(10, ldt.dayOfMonth)
        assertEquals(13, ldt.hour)
        assertEquals(55, ldt.minute)
        assertEquals(21, ldt.second)
    }

}