package net.nemerosa.ontrack.model

import net.nemerosa.ontrack.json.ObjectMapperFactory.create
import org.junit.jupiter.api.Test
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AckTest {
    @Test
    fun ok() {
        assertTrue(Ack.OK.success)
    }

    @Test
    fun nok() {
        assertFalse(Ack.NOK.success)
    }

    @Test
    fun validate_true() {
        assertTrue(Ack.validate(true).success)
    }

    @Test
    fun validate_false() {
        assertFalse(Ack.validate(false).success)
    }

    @Test
    fun one_0() {
        assertFalse(Ack.one(0).success)
    }

    @Test
    fun one_1() {
        assertTrue(Ack.one(1).success)
    }

    @Test
    fun one_more() {
        assertFalse(Ack.one(2).success)
    }

    @Test
    @Throws(IOException::class)
    fun to_json() {
        val mapper = create()
        val value = mapper.writeValueAsString(Ack.OK)
        assertEquals("{\"success\":true}", value)
    }

    @Test
    fun and() {
        assertFalse(Ack.NOK.and(Ack.NOK).success)
        assertFalse(Ack.NOK.and(Ack.OK).success)
        assertFalse(Ack.OK.and(Ack.NOK).success)
        assertTrue(Ack.OK.and(Ack.OK).success)
    }

    @Test
    fun or() {
        assertFalse(Ack.NOK.or(Ack.NOK).success)
        assertTrue(Ack.NOK.or(Ack.OK).success)
        assertTrue(Ack.OK.or(Ack.NOK).success)
        assertTrue(Ack.OK.or(Ack.OK).success)
    }
}
