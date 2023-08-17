package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BuildLinkFormTest {

    @Test
    fun json_read_legacy() {
        assertEquals(
            BuildLinkForm(
                addOnly = true,
                links = listOf(
                    BuildLinkFormItem("P1", "B1", ""),
                    BuildLinkFormItem("P2", "B2", ""),
                )
            ),
            mapOf(
                "addOnly" to true,
                "links" to listOf(
                    mapOf(
                        "project" to "P1",
                        "build" to "B1",
                    ),
                    mapOf(
                        "project" to "P2",
                        "build" to "B2",
                    ),
                )
            ).asJson().parse()
        )
    }

    @Test
    fun json_read() {
        assertEquals(
            BuildLinkForm(
                addOnly = true,
                links = listOf(
                    BuildLinkFormItem("P1", "B1", ""),
                    BuildLinkFormItem("P2", "B2", ""),
                )
            ),
            mapOf(
                "addOnly" to true,
                "links" to listOf(
                    mapOf(
                        "project" to "P1",
                        "build" to "B1",
                        "qualifier" to ""
                    ),
                    mapOf(
                        "project" to "P2",
                        "build" to "B2",
                        "qualifier" to ""
                    ),
                )
            ).asJson().parse()
        )
    }

    @Test
    fun json_write() {
        assertEquals(
            mapOf(
                "addOnly" to true,
                "links" to listOf(
                    mapOf(
                        "project" to "P1",
                        "build" to "B1",
                        "qualifier" to "dep1"
                    ),
                    mapOf(
                        "project" to "P2",
                        "build" to "B2",
                        "qualifier" to ""
                    ),
                )
            ).asJson(),
            BuildLinkForm(
                addOnly = true,
                links = listOf(
                    BuildLinkFormItem("P1", "B1", "dep1"),
                    BuildLinkFormItem("P2", "B2", ""),
                )
            ).asJson()
        )
    }
}
