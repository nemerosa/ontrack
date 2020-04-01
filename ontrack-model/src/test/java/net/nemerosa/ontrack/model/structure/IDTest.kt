package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.node.IntNode
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.structure.ID.Companion.isDefined
import net.nemerosa.ontrack.model.structure.ID.Companion.of
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class IDTest {
    @Test
    fun none() {
        val id = ID.NONE
        assertNotNull(id)
        assertFalse(id.isSet)
        assertEquals(0, id.value.toLong())
        assertEquals("0", id.toString())
    }

    @Test
    fun set() {
        val id = of(1)
        assertNotNull(id)
        assertTrue(id.isSet)
        assertEquals(1, id.value.toLong())
        assertEquals("1", id.toString())
    }

    @Test(expected = IllegalArgumentException::class)
    fun not_zero() {
        of(0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun not_negative() {
        of(-1)
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun set_to_json() {
        TestUtils.assertJsonWrite(
                JsonUtils.number(12),
                of(12)
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun read_from_json() {
        TestUtils.assertJsonRead(
                of(9),
                IntNode(9),
                ID::class.java
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun unset_to_json() {
        TestUtils.assertJsonWrite(
                JsonUtils.number(0),
                ID.NONE
        )
    }

    @Test
    fun is_defined_null() {
        assertFalse(isDefined(null))
    }

    @Test
    fun is_defined_none() {
        assertFalse(isDefined(ID.NONE))
    }

    @Test
    fun is_defined_set() {
        assertTrue(isDefined(of(1)))
    }
}