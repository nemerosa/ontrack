package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.json.isNullOrNullNode
import net.nemerosa.ontrack.model.security.ProjectCreation
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchFavouriteService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BranchGraphQLIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var branchFavouriteService: BranchFavouriteService

    @Test
    fun `Creating a branch for a project name`() {
        asAdmin {
            project {
                val data = run("""
                    mutation {
                        createBranch(input: {projectName: "$name", name: "main"}) {
                            branch {
                                id
                                name
                            }
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """)
                // Checks the branch has been created
                assertNotNull(structureService.findBranchByName(name, "main").getOrNull(), "Branch has been created") {
                    assertFalse(it.isDisabled, "Branch is not disabled")
                }
                // Checks the data
                val branch = data["createBranch"]["branch"]
                assertTrue(branch["id"].asInt() > 0, "ID is set")
                assertEquals("main", branch["name"].asText(), "Name is OK")
                assertTrue(data["createBranch"]["errors"].isNullOrNullNode(), "No error")
            }
        }
    }

    @Test
    fun `Creating a branch for a project ID`() {
        asAdmin {
            project {
                val data = run("""
                    mutation {
                        createBranch(input: {projectId: ${id}, name: "main"}) {
                            branch {
                                id
                                name
                            }
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """)
                // Checks the branch has been created
                assertNotNull(structureService.findBranchByName(name, "main").getOrNull(), "Branch has been created") {
                    assertFalse(it.isDisabled, "Branch is not disabled")
                }
                // Checks the data
                val branch = data["createBranch"]["branch"]
                assertTrue(branch["id"].asInt() > 0, "ID is set")
                assertEquals("main", branch["name"].asText(), "Name is OK")
                assertTrue(data["createBranch"]["errors"].isNullOrNullNode(), "No error")
            }
        }
    }

    @Test
    fun `Creating a branch with missing project ID and name`() {
        asAdmin {
            project {
                val data = run("""
                    mutation {
                        createBranch(input: {name: "main"}) {
                            branch {
                                id
                                name
                            }
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """)
                // Checks the errors
                val error = data["createBranch"]["errors"][0]
                assertEquals("Project ID or name is required", error["message"].asText())
                assertEquals("net.nemerosa.ontrack.graphql.schema.ProjectIdOrNameMissingException", error["exception"].asText())
                assertTrue(data["createBranch"]["branch"].isNullOrNullNode(), "Branch not returned")
            }
        }
    }

    @Test
    fun `Creating a branch with both project ID and name`() {
        asAdmin {
            project {
                val data = run("""
                    mutation {
                        createBranch(input: {projectId: $id, projectName: "$name", name: "main"}) {
                            branch {
                                id
                                name
                            }
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """)
                // Checks the errors
                val error = data["createBranch"]["errors"][0]
                assertEquals("Project ID or name is required, not both.", error["message"].asText())
                assertEquals("net.nemerosa.ontrack.graphql.schema.ProjectIdAndNameProvidedException", error["exception"].asText())
                assertTrue(data["createBranch"]["branch"].isNullOrNullNode(), "Branch not returned")
            }
        }
    }

    @Test
    fun `Creating a branch for a project ID but name already exists`() {
        asAdmin {
            project {
                branch(name = "main")
                val data = run("""
                    mutation {
                        createBranch(input: {projectId: ${id}, name: "main"}) {
                            branch {
                                id
                                name
                            }
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """)
                // Checks the errors
                val error = data["createBranch"]["errors"][0]
                assertEquals("Branch name already exists: main", error["message"].asText())
                assertEquals("net.nemerosa.ontrack.model.exceptions.BranchNameAlreadyDefinedException", error["exception"].asText())
                assertTrue(data["createBranch"]["branch"].isNullOrNullNode(), "Branch not returned")
            }
        }
    }

    @Test
    fun `Creating a branch for a project ID but name is invalid`() {
        asAdmin {
            project {
                val data = run("""
                    mutation {
                        createBranch(input: {projectId: ${id}, name: "main with space"}) {
                            branch {
                                id
                                name
                            }
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """)
                // Checks the errors
                val error = data["createBranch"]["errors"][0]
                assertEquals("The name can only have letters, digits, dots (.), dashes (-) or underscores (_).", error["message"].asText())
                assertEquals("net.nemerosa.ontrack.graphql.support.MutationInputValidationException", error["exception"].asText())
                assertTrue(data["createBranch"]["branch"].isNullOrNullNode(), "Branch not returned")
            }
        }
    }

    @Test
    fun `Creating a branch in get mode from a project ID`() {
        asAdmin {
            project {
                val data = run(
                    """
                        mutation  {
                            createBranchOrGet(input: {projectId: $id, name: "main"}) {
                                branch {
                                    id
                                    name
                                }
                                errors {
                                    message
                                    exception
                                }
                            }
                        }
                    """
                )
                // Checks the errors
                assertNoUserError(data, "createBranchOrGet")
                val node = data["createBranchOrGet"]["branch"]
                assertEquals("main", node["name"].asText())
            }
        }
    }

    @Test
    fun `Creating a branch in get mode from a project name`() {
        asAdmin {
            project {
                val data = run(
                    """
                        mutation  {
                            createBranchOrGet(input: {projectName: "$name", name: "main"}) {
                                branch {
                                    id
                                    name
                                }
                                errors {
                                    message
                                    exception
                                }
                            }
                        }
                    """
                )
                // Checks the errors
                assertNoUserError(data, "createBranchOrGet")
                val node = data["createBranchOrGet"]["branch"]
                assertEquals("main", node["name"].asText())
            }
        }
    }

    @Test
    fun `Creating a branch in get mode for an existing branch from a project ID`() {
        asAdmin {
            project {
                val branch = branch(name = "main")
                val data = run(
                    """
                        mutation  {
                            createBranchOrGet(input: {projectId: $id, name: "main"}) {
                                branch {
                                    id
                                    name
                                }
                                errors {
                                    message
                                    exception
                                }
                            }
                        }
                    """
                )
                // Checks the errors
                assertNoUserError(data, "createBranchOrGet")
                val node = data["createBranchOrGet"]["branch"]
                assertEquals("main", node["name"].asText())
                assertEquals(branch.id(), node["id"].asInt())
            }
        }
    }

    @Test
    fun `Creating a branch in get mode for an existing branch from a project name`() {
        asAdmin {
            project {
                val branch = branch(name = "main")
                val data = run(
                    """
                        mutation  {
                            createBranchOrGet(input: {projectName: "$name", name: "main"}) {
                                branch {
                                    id
                                    name
                                }
                                errors {
                                    message
                                    exception
                                }
                            }
                        }
                    """
                )
                // Checks the errors
                assertNoUserError(data, "createBranchOrGet")
                val node = data["createBranchOrGet"]["branch"]
                assertEquals("main", node["name"].asText())
                assertEquals(branch.id(), node["id"].asInt())
            }
        }
    }

    @Test
    fun `All favourite branches`() {
        val account = doCreateAccount()
        val branch1 = project<Branch> {
            branch {}
            branch {
                asConfigurableAccount(account).withView(this).execute {
                    branchFavouriteService.setBranchFavourite(this, true)
                }
            }
        }
        val branch2 = project<Branch> {
            branch {}
            branch {
                asConfigurableAccount(account).withView(this).execute {
                    branchFavouriteService.setBranchFavourite(this, true)
                }
            }
        }
        // Gets ALL the favourite branches
        val data = asConfigurableAccount(account).withView(branch1).withView(branch2).call {
            run("""
                    {
                        branches(favourite: true) {
                            id
                        }
                    }
                """)
        }
        val branchIds: Set<Int> = data["branches"].map { it["id"].asInt() }.toSet()
        assertEquals(
            setOf(branch1.id(), branch2.id()),
            branchIds
        )
    }

    @Test
    fun `Favourite branch on one project`() {
        val account = doCreateAccount()
        project {
            val fav = branch {
                asConfigurableAccount(account).withView(this).execute {
                    branchFavouriteService.setBranchFavourite(this, true)
                }
            }
            branch {}
            // Gets the favourite branches
            val data = asConfigurableAccount(account).withView(this).call {
                run("""
                    {
                        branches(project: "${this.name}", favourite: true) {
                            id
                        }
                    }
                """)
            }
            val branchIds: Set<Int> = data["branches"].map { it["id"].asInt() }.toSet()
            assertEquals(
                setOf(fav.id()),
                branchIds
            )
        }
    }

    @Test
    fun `Branch by name on two different projects`() {
        val name = uid("B")

        val p1 = doCreateProject()
        val b1 = doCreateBranch(p1, NameDescription.nd(name, ""))
        doCreateBranch(p1, NameDescription.nd("B2", ""))
        val p2 = doCreateProject()
        val b2 = doCreateBranch(p2, NameDescription.nd(name, ""))

        val data = run("""{branches (name: "$name") { id } }""")
        assertEquals(
            setOf(b1.id(), b2.id()),
            data["branches"].map { it["id"].asInt() }.toSet()
        )
    }

    @Test
    fun `Last promotion run only`() {
        // Creates a branch
        val branch = doCreateBranch()
        // ... a promotion level
        val pl = doCreatePromotionLevel(branch, NameDescription.nd("COPPER", ""))
        // ... one build
        val build = doCreateBuild(branch, NameDescription.nd("1", ""))
        // ... and promotes it twice
        doPromote(build, pl, "Once")
        doPromote(build, pl, "Twice")
        // Asks for the promotion runs of the build
        val data = run("""{
            |branches(id: ${branch.id}) {
            |   builds {
            |       promotionRuns(lastPerLevel: true) {
            |           promotionLevel {
            |               name
            |           }
            |           description
            |       }
            |   }
            |}
            |}""".trimMargin())
        // Gets the first build
        val buildNode = data.path("branches").get(0).path("builds").get(0)
        // Gets the promotion runs
        val promotionRuns = buildNode.path("promotionRuns")
        assertEquals(1, promotionRuns.size())
        val promotionRun = promotionRuns.get(0)
        assertEquals("Twice", promotionRun.path("description").asText())
        assertEquals("COPPER", promotionRun.path("promotionLevel").path("name").asText())
    }

}