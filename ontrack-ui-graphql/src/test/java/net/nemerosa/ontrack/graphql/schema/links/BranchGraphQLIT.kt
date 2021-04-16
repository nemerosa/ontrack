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

}