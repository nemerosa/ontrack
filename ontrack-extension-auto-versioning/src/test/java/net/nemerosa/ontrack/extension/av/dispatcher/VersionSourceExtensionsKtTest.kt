package net.nemerosa.ontrack.extension.av.dispatcher

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class VersionSourceExtensionsKtTest {

    @Test
    fun `Version source config with two components`() {
        val (id, config) = getVersionSourceConfig("metaInfo/name/value")
        assertEquals("metaInfo", id)
        assertEquals("name/value", config)
    }

    @Test
    fun `Version source config with one component`() {
        val (id, config) = getVersionSourceConfig("labelOnly")
        assertEquals("labelOnly", id)
        assertEquals(null, config)
    }

}