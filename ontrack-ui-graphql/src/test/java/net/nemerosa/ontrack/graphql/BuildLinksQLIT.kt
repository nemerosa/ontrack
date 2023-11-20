package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.it.forRecursiveLinks
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BuildLinksQLIT : AbstractQLKTITSupport() {

    @Test
    fun `Getting first level builds by default`() {
        forRecursiveLinks { _, p, builds ->
            run(
                """
                    {
                        build(id: ${p.id}) {
                            usingQualified {
                                pageItems {
                                    build {
                                        name
                                    }
                                }
                            }
                        }
                    }
                """
            ) { data ->
                val names = data.path("build")
                    .path("usingQualified").path("pageItems")
                    .map {
                        it.path("build").path("name").asText()
                    }
                assertEquals(
                    listOf(
                        builds["q2"]?.name,
                        builds["q1"]?.name,
                    ),
                    names
                )
            }
        }
    }

    @Test
    fun `Filtering builds on project labels`() {
        forRecursiveLinks { label, p, builds ->
            run(
                """
                    {
                        build(id: ${p.id}) {
                            usingQualified(label: "${label.getDisplay()}") {
                                pageItems {
                                    build {
                                        name
                                    }
                                }
                            }
                        }
                    }
            """
            ) { data ->
                val names = data.path("build")
                    .path("usingQualified").path("pageItems")
                    .map {
                        it.path("build").path("name").asText()
                    }
                assertEquals(
                    listOf(
                        builds["q1"]?.name,
                    ),
                    names
                )
            }
        }
    }

    @Test
    fun `Getting recursive links with max depth`() {
        forRecursiveLinks { _, p, builds ->
            run(
                """
                    {
                        build(id: ${p.id}) {
                            usingQualified(depth: 1) {
                                pageItems {
                                    build {
                                        name
                                    }
                                }
                            }
                        }
                    }
            """
            ) { data ->
                val names = data.path("build")
                    .path("usingQualified").path("pageItems")
                    .map {
                        it.path("build").path("name").asText()
                    }
                assertEquals(
                    listOf(
                        builds["q2"]?.name,
                        builds["r5"]?.name,
                        builds["r4"]?.name,
                        builds["r3"]?.name,
                        builds["q1"]?.name,
                        builds["r2"]?.name,
                        builds["r1"]?.name,
                    ),
                    names
                )
            }
        }
    }

    @Test
    fun `Getting recursive links with max depth and label filtering`() {
        forRecursiveLinks { label, p, builds ->
            run(
                """
                    {
                        build(id: ${p.id}) {
                            usingQualified(depth: 1, label: "${label.getDisplay()}") {
                                pageItems {
                                    build {
                                        name
                                    }
                                }
                            }
                        }
                    }
            """
            ) { data ->
                val names = data.path("build")
                    .path("usingQualified").path("pageItems")
                    .map {
                        it.path("build").path("name").asText()
                    }
                assertEquals(
                    listOf(
                        builds["r4"]?.name,
                        builds["q1"]?.name,
                        builds["r1"]?.name,
                    ),
                    names
                )
            }
        }
    }

    @Test
    fun `Getting recursive links with deep depth and label filtering`() {
        forRecursiveLinks { label, p, builds ->
            run(
                """
                    {
                        build(id: ${p.id}) {
                            usingQualified(depth: 10, label: "${label.getDisplay()}") {
                                pageItems {
                                    build {
                                        name
                                    }
                                }
                            }
                        }
                    }
            """
            ) { data ->
                val names = data.path("build")
                    .path("usingQualified").path("pageItems")
                    .map {
                        it.path("build").path("name").asText()
                    }
                assertEquals(
                    listOf(
                        builds["r4"]?.name,
                        builds["q1"]?.name,
                        builds["s2"]?.name,
                        builds["r1"]?.name,
                    ),
                    names
                )
            }
        }
    }

}