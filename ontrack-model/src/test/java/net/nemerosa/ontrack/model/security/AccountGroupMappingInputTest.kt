package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.structure.ID
import org.junit.Test
import kotlin.test.assertEquals

class AccountGroupMappingInputTest {

    @Test
    fun jsonRead() {
        val json = JsonUtils.parseAsNode("""{"name":"M11242253","group":9}""")
        val input = JsonUtils.parse(json, AccountGroupMappingInput::class.java)
        assertEquals("M11242253", input.name)
        assertEquals(ID.of(9), input.group)
    }

}
