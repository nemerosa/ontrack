package net.nemerosa.ontrack.common

import org.junit.Test
import kotlin.test.assertEquals

class KTUtilsTest {

    @Test
    fun asMap() {
        assertEquals(
                mapOf(
                        "id" to 10,
                        "name" to "Some name"
                ),
                Pojo(10, "Some name", "Some description").run {
                    asMap(
                            this::id,
                            this::name
                    )
                }
        )
    }

    class Pojo(
            val id: Int,
            val name: String,
            val description: String?
    )

}