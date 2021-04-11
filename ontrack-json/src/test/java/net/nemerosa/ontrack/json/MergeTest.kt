package net.nemerosa.ontrack.json

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Testing JSON merge
 */
class MergeTest {

    @Test
    fun `Simple objects`() {
        val left = mapOf("firstName" to "Damien").asJson()
        val right = mapOf("lastName" to "Coraboeuf").asJson()
        assertEquals(
            mapOf(
                "firstName" to "Damien",
                "lastName" to "Coraboeuf",
            ).asJson(),
            left.merge(right)
        )
    }

    @Test
    fun `Right component takes precedence by default`() {
        val left = mapOf("firstName" to "Damien").asJson()
        val right = mapOf("firstName" to "Julien").asJson()
        assertEquals(
            mapOf(
                "firstName" to "Julien",
            ).asJson(),
            left.merge(right)
        )
    }

    @Test
    fun `Left component takes precedence if configured`() {
        val left = mapOf("firstName" to "Damien").asJson()
        val right = mapOf("firstName" to "Julien").asJson()
        assertEquals(
            mapOf(
                "firstName" to "Damien",
            ).asJson(),
            left.merge(right, priority = JsonMergePriority.LEFT)
        )
    }

    @Test
    fun `Deeper objects`() {
        val left = mapOf(
            "config" to mapOf(
                "Damien" to "parent",
            )
        ).asJson()
        val right = mapOf(
            "config" to mapOf(
                "Julien" to "child",
            )
        ).asJson()
        assertEquals(
            mapOf(
                "config" to mapOf(
                    "Damien" to "parent",
                    "Julien" to "child",
                )
            ).asJson(),
            left.merge(right)
        )
    }

    @Test
    fun `Arrays are appended by default`() {
        val left = mapOf(
            "list" to listOf(
                mapOf(
                    "id" to 1,
                    "name" to "One"
                ),
                mapOf(
                    "id" to 2,
                    "name" to "Two"
                ),
            )
        ).asJson()
        val right = mapOf(
            "list" to listOf(
                mapOf(
                    "id" to 3,
                    "name" to "Three"
                ),
            )
        ).asJson()
        assertEquals(
            mapOf(
                "list" to listOf(
                    mapOf(
                        "id" to 1,
                        "name" to "One"
                    ),
                    mapOf(
                        "id" to 2,
                        "name" to "Two"
                    ),
                    mapOf(
                        "id" to 3,
                        "name" to "Three"
                    ),
                )
            ).asJson(),
            left.merge(right)
        )
    }

    @Test
    fun `Arrays can be kept`() {
        val left = mapOf(
            "list" to listOf(
                mapOf(
                    "id" to 1,
                    "name" to "One"
                ),
                mapOf(
                    "id" to 2,
                    "name" to "Two"
                ),
            )
        ).asJson()
        val right = mapOf(
            "list" to listOf(
                mapOf(
                    "id" to 3,
                    "name" to "Three"
                ),
            )
        ).asJson()
        assertEquals(
            mapOf(
                "list" to listOf(
                    mapOf(
                        "id" to 1,
                        "name" to "One"
                    ),
                    mapOf(
                        "id" to 2,
                        "name" to "Two"
                    ),
                )
            ).asJson(),
            left.merge(right, arrays = JsonArrayMergePriority.LEFT)
        )
    }

    @Test
    fun `Arrays can be replaced`() {
        val left = mapOf(
            "list" to listOf(
                mapOf(
                    "id" to 1,
                    "name" to "One"
                ),
                mapOf(
                    "id" to 2,
                    "name" to "Two"
                ),
            )
        ).asJson()
        val right = mapOf(
            "list" to listOf(
                mapOf(
                    "id" to 3,
                    "name" to "Three"
                ),
            )
        ).asJson()
        assertEquals(
            mapOf(
                "list" to listOf(
                    mapOf(
                        "id" to 3,
                        "name" to "Three"
                    ),
                )
            ).asJson(),
            left.merge(right, arrays = JsonArrayMergePriority.RIGHT)
        )
    }

    @Test
    fun `Deep test`() {
        val left = mapOf(
            "ontrack" to mapOf(
                "config" to mapOf(
                    "settings" to mapOf(
                        "general" to mapOf(
                            "grantViewToAll" to true,
                        ),
                    ),
                    "groups" to listOf(
                        mapOf(
                            "name" to "developers",
                        ),
                    ),
                    "oidc" to listOf(
                        mapOf(
                            "id" to "one",
                            "name" to "One",
                        )
                    )
                ),
            ),
        ).asJson()
        val right = mapOf(
            "ontrack" to mapOf(
                "config" to mapOf(
                    "settings" to mapOf(
                        "homePage" to mapOf(
                            "maxProjects" to 100,
                        ),
                    ),
                    "groups" to listOf(
                        mapOf(
                            "name" to "testers",
                        ),
                    ),
                    "oidc" to listOf(
                        mapOf(
                            "id" to "two",
                            "name" to "Two",
                        )
                    )
                ),
            ),
        ).asJson()

        assertEquals(
            mapOf(
                "ontrack" to mapOf(
                    "config" to mapOf(
                        "settings" to mapOf(
                            "general" to mapOf(
                                "grantViewToAll" to true,
                            ),
                            "homePage" to mapOf(
                                "maxProjects" to 100,
                            ),
                        ),
                        "groups" to listOf(
                            mapOf(
                                "name" to "developers",
                            ),
                            mapOf(
                                "name" to "testers",
                            ),
                        ),
                        "oidc" to listOf(
                            mapOf(
                                "id" to "one",
                                "name" to "One",
                            ),
                            mapOf(
                                "id" to "two",
                                "name" to "Two",
                            )
                        )
                    ),
                ),
            ).asJson(),
            left.merge(right)
        )
    }

}