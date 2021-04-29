package net.nemerosa.ontrack.graphql.schema.links

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.Test
import kotlin.test.assertEquals

class BranchGraphQLIT : AbstractQLKTITSupport() {

    @Test
    fun `Two layers`() {
        withLinks {
            val project = build("project", 1)
            val component = build("component", 1)
            val library = build("library", 1)
            component linkTo library
            project linkTo component

            run("""query {
                branches(id: ${project.branch.id}) {
                    graph(direction: USING) {
                        ...nodeContent
                        edges {
                            linkedTo {
                                ...nodeContent
                                edges {
                                    linkedTo {
                                        ...nodeContent
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            fragment nodeContent on BranchLinksNode {
                branch {
                    name
                    project {
                        name
                    }
                }
            }
            """).let { data ->
                assertEquals(
                    mapOf(
                        "branches" to listOf(
                            mapOf(
                                "graph" to mapOf(
                                    "branch" to mapOf(
                                        "name" to project.branch.name,
                                        "project" to mapOf(
                                            "name" to project.project.name
                                        )
                                    ),
                                    "edges" to listOf(
                                        mapOf(
                                            "linkedTo" to mapOf(
                                                "branch" to mapOf(
                                                    "name" to component.branch.name,
                                                    "project" to mapOf(
                                                        "name" to component.project.name
                                                    )
                                                ),
                                                "edges" to listOf(
                                                    mapOf(
                                                        "linkedTo" to mapOf(
                                                            "branch" to mapOf(
                                                                "name" to library.branch.name,
                                                                "project" to mapOf(
                                                                    "name" to library.project.name
                                                                )
                                                            )
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

    @Test
    fun `Two layers in used by direction`() {
        withLinks {
            val project = build("project", 1)
            val component = build("component", 1)
            val library = build("library", 1)
            component linkTo library
            project linkTo component

            run("""query {
                branches(id: ${library.branch.id}) {
                    graph(direction: USED_BY) {
                        ...nodeContent
                        edges {
                            linkedTo {
                                ...nodeContent
                                edges {
                                    linkedTo {
                                        ...nodeContent
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            fragment nodeContent on BranchLinksNode {
                branch {
                    name
                    project {
                        name
                    }
                }
            }
            """).let { data ->
                assertEquals(
                    mapOf(
                        "branches" to listOf(
                            mapOf(
                                "graph" to mapOf(
                                    "branch" to mapOf(
                                        "name" to library.branch.name,
                                        "project" to mapOf(
                                            "name" to library.project.name
                                        )
                                    ),
                                    "edges" to listOf(
                                        mapOf(
                                            "linkedTo" to mapOf(
                                                "branch" to mapOf(
                                                    "name" to component.branch.name,
                                                    "project" to mapOf(
                                                        "name" to component.project.name
                                                    )
                                                ),
                                                "edges" to listOf(
                                                    mapOf(
                                                        "linkedTo" to mapOf(
                                                            "branch" to mapOf(
                                                                "name" to project.branch.name,
                                                                "project" to mapOf(
                                                                    "name" to project.project.name
                                                                )
                                                            )
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

    @Test
    fun `Build graph in 'using' direction with one filled layer`() {
        withLinks {
            build("project", 1) linkTo build("component", 1)

            run("""query {
                builds(id: ${build("project", 1).id}) {
                    graph(direction: USING) {
                        ...nodeContent
                        edges {
                            linkedTo {
                                ...nodeContent
                                edges {
                                    linkedTo {
                                        ...nodeContent
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            fragment nodeContent on BranchLinksNode {
                branch {
                    name
                    project {
                        name
                    }
                }
                build {
                    name
                }
            }
            """).let { data ->
                assertEquals(
                    mapOf(
                        "builds" to listOf(
                            mapOf(
                                "graph" to mapOf(
                                    "branch" to mapOf(
                                        "name" to build("project", 1).branch.name,
                                        "project" to mapOf(
                                            "name" to build("project", 1).project.name
                                        )
                                    ),
                                    "build" to mapOf(
                                        "name" to build("project", 1).name
                                    ),
                                    "edges" to listOf(
                                        mapOf(
                                            "linkedTo" to mapOf(
                                                "branch" to mapOf(
                                                    "name" to build("component", 1).branch.name,
                                                    "project" to mapOf(
                                                        "name" to build("component", 1).project.name
                                                    )
                                                ),
                                                "build" to mapOf(
                                                    "name" to build("component", 1).name
                                                ),
                                                "edges" to emptyList<String>())
                                        )
                                    )
                                )
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

    @Test
    fun `Build graph in 'used by' direction with one filled layer`() {
        withLinks {
            build("project", 1) linkTo build("component", 1)

            run("""query {
                builds(id: ${build("component", 1).id}) {
                    graph(direction: USED_BY) {
                        ...nodeContent
                        edges {
                            linkedTo {
                                ...nodeContent
                                edges {
                                    linkedTo {
                                        ...nodeContent
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            fragment nodeContent on BranchLinksNode {
                branch {
                    name
                    project {
                        name
                    }
                }
                build {
                    name
                }
            }
            """).let { data ->
                assertEquals(
                    mapOf(
                        "builds" to listOf(
                            mapOf(
                                "graph" to mapOf(
                                    "branch" to mapOf(
                                        "name" to build("component", 1).branch.name,
                                        "project" to mapOf(
                                            "name" to build("component", 1).project.name
                                        )
                                    ),
                                    "build" to mapOf(
                                        "name" to build("component", 1).name
                                    ),
                                    "edges" to listOf(
                                        mapOf(
                                            "linkedTo" to mapOf(
                                                "branch" to mapOf(
                                                    "name" to build("project", 1).branch.name,
                                                    "project" to mapOf(
                                                        "name" to build("project", 1).project.name
                                                    )
                                                ),
                                                "build" to mapOf(
                                                    "name" to build("project", 1).name
                                                ),
                                                "edges" to emptyList<String>())
                                        )
                                    )
                                )
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

    @Test
    fun `Build graph in 'used by' direction with one unfilled layer`() {
        withLinks {
            build("project", 1) linkTo build("component", 1)
            build("component", 2)

            run("""query {
                builds(id: ${build("component", 2).id}) {
                    graph(direction: USED_BY) {
                        ...nodeContent
                        edges {
                            linkedTo {
                                ...nodeContent
                                edges {
                                    linkedTo {
                                        ...nodeContent
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            fragment nodeContent on BranchLinksNode {
                branch {
                    name
                    project {
                        name
                    }
                }
                build {
                    name
                }
            }
            """).let { data ->
                assertEquals(
                    mapOf(
                        "builds" to listOf(
                            mapOf(
                                "graph" to mapOf(
                                    "branch" to mapOf(
                                        "name" to build("component", 2).branch.name,
                                        "project" to mapOf(
                                            "name" to build("component", 2).project.name
                                        )
                                    ),
                                    "build" to mapOf(
                                        "name" to build("component", 2).name
                                    ),
                                    "edges" to listOf(
                                        mapOf(
                                            "linkedTo" to mapOf(
                                                "branch" to mapOf(
                                                    "name" to build("project", 1).branch.name,
                                                    "project" to mapOf(
                                                        "name" to build("project", 1).project.name
                                                    )
                                                ),
                                                "build" to null,
                                                "edges" to emptyList<String>())
                                        )
                                    )
                                )
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

    @Test
    fun `Discarding old branches when building the graph`() {
        withBranchLinkSettings(history = 2) {
            withLinks {

                val component = project("component")
                val componentFeature = component.branch(name = "feature")
                val componentFeature1 = componentFeature.build("1")
                val componentMain = component.branch(name = "main")
                val componentMain1 = componentMain.build("21")
                val componentMain2 = componentMain.build("22")

                val projectA = "projectA"
                build(projectA, 1) linkTo componentFeature1
                build(projectA, 2) linkTo componentFeature1
                build(projectA, 3) linkTo componentMain1
                build(projectA, 4) linkTo componentMain2

                val query = """
                    query Graph(${'$'}branchId: Int!) {
                        branches(id: ${'$'}branchId) {
                            graph(direction: USING) {
                                branch {
                                    name
                                    project {
                                        name
                                    }
                                }
                                edges {
                                    linkedTo {
                                        branch {
                                            name
                                            project {
                                                name
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                """

                run(query, mapOf("branchId" to branch(projectA).id())).let { data ->
                    assertEquals(
                        mapOf(
                            "branches" to listOf(
                                mapOf(
                                    "graph" to mapOf(
                                        "branch" to mapOf(
                                            "name" to branch(projectA).name,
                                            "project" to mapOf(
                                                "name" to project(projectA).name
                                            )
                                        ),
                                        "edges" to listOf(
                                            mapOf(
                                                "linkedTo" to mapOf(
                                                    "branch" to mapOf(
                                                        "name" to componentMain.name,
                                                        "project" to mapOf(
                                                            "name" to component.name
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        ).asJson(),
                        data
                    )
                }

                val projectB = "projectB"
                build(projectB, 1) linkTo componentFeature1
                build(projectB, 2) linkTo componentFeature1
                build(projectB, 3) linkTo componentMain1

                run(query, mapOf("branchId" to branch(projectB).id())).let { data ->
                    assertEquals(
                        mapOf(
                            "branches" to listOf(
                                mapOf(
                                    "graph" to mapOf(
                                        "branch" to mapOf(
                                            "name" to branch(projectB).name,
                                            "project" to mapOf(
                                                "name" to project(projectB).name
                                            )
                                        ),
                                        "edges" to listOf(
                                            mapOf(
                                                "linkedTo" to mapOf(
                                                    "branch" to mapOf(
                                                        "name" to componentMain.name,
                                                        "project" to mapOf(
                                                            "name" to component.name
                                                        )
                                                    )
                                                )
                                            ),
                                            mapOf(
                                                "linkedTo" to mapOf(
                                                    "branch" to mapOf(
                                                        "name" to componentFeature.name,
                                                        "project" to mapOf(
                                                            "name" to component.name
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        ).asJson(),
                        data
                    )
                }

            }
        }
    }

}