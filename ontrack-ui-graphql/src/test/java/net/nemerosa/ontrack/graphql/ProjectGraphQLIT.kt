package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.json.isNullOrNullNode
import net.nemerosa.ontrack.model.security.ProjectCreation
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.structure.BranchFavouriteService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.assertNotPresent
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import kotlin.test.*

class ProjectGraphQLIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var branchFavouriteService: BranchFavouriteService
    @Test
    fun `Looking for projects using a pattern`() {
        val rootA = TestUtils.uid("P")
        val rootB = TestUtils.uid("P")
        repeat(5) {
            project(name = NameDescription.nd("X${rootA}$it", ""))
        }
        repeat(5) {
            project(name = NameDescription.nd("Y${rootB}$it", ""))
        }
        asAdmin {
            run("""{
                projects(pattern: "X$rootA") {
                    name
                }
            }""").let { data ->
                val names = data.path("projects").map { it.path("name").asText() }
                assertEquals(
                    (0..4).map { "X$rootA$it" },
                    names
                )
            }
        }
    }

    @Test
    fun `Looking for projects using a pattern is restricted by authorizations`() {
        val rootA = TestUtils.uid("P")
        val rootB = TestUtils.uid("P")
        val projectsA = (0..4).map {
            project(name = NameDescription.nd("X${rootA}$it", ""))
        }
        repeat(5) {
            project(name = NameDescription.nd("Y${rootB}$it", ""))
        }
        withNoGrantViewToAll {
            asUserWithView(*projectsA.take(3).toTypedArray()) {
                run("""{
                    projects(pattern: "X$rootA") {
                        name
                    }
                }""").let { data ->
                    val names = data.path("projects").map { it.path("name").asText() }
                    assertEquals(
                        (0..2).map { "X$rootA$it" },
                        names
                    )
                }
            }
        }
    }

    @Test
    fun `Maximum number of branches`() {
        asAdmin {
            project {
                // Creates 20 branches
                repeat(20) {
                    branch(name = "1.$it")
                }
                // Query for the last 10
                run("""{
                    projects(id: $id) {
                        branches(count: 10) {
                            name
                        }
                    }
                }""").let { data ->
                    val names = data.path("projects").path(0).path("branches").map {
                        it.path("name").asText()
                    }
                    assertEquals(
                        names,
                        (19 downTo 10).map {
                            "1.$it"
                        }
                    )
                }
            }
        }
    }


    @Test
    fun `Creating a project`() {
        asAdmin {
            val name = uid("P")
            val data = run("""
                mutation CreateProject(${'$'}name: String!) {
                    createProject(input: {name: ${'$'}name}) {
                        project {
                            id
                            name
                        }
                        errors {
                            message
                        }
                    }
                }
            """, mapOf("name" to name))
            // Checks the project has been created
            assertNotNull(structureService.findProjectByName(name).getOrNull(), "Project has been created") {
                assertFalse(it.isDisabled, "Project is not disabled")
            }
            // Checks the data
            val project = data["createProject"]["project"]
            assertTrue(project["id"].asInt() > 0, "ID is set")
            assertEquals(name, project["name"].asText(), "Name is OK")
            assertTrue(data["createProject"]["errors"].isNullOrNullNode(), "No error")
        }
    }

    @Test
    fun `Creating a project but name already exist`() {
        asAdmin {
            project {
                val data = run("""
                    mutation CreateProject(${'$'}name: String!) {
                        createProject(input: {name: ${'$'}name}) {
                            project {
                                id
                                name
                            }
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """, mapOf("name" to name))
                // Checks the errors
                val error = data["createProject"]["errors"][0]
                assertEquals("Project name already exists: $name", error["message"].asText())
                assertEquals("net.nemerosa.ontrack.model.exceptions.ProjectNameAlreadyDefinedException", error["exception"].asText())
                assertTrue(data["createProject"]["project"].isNullOrNullNode(), "Project not returned")
            }
        }
    }

    @Test
    fun `Creating a project with an invalid name`() {
        asAdmin {
            val data = run("""
                mutation CreateProject(${'$'}name: String!) {
                    createProject(input: {name: ${'$'}name}) {
                        project {
                            id
                            name
                        }
                        errors {
                            message
                            exception
                            location
                        }
                    }
                }
            """, mapOf("name" to "white space"))
            // Checks the errors
            val error = data["createProject"]["errors"][0]
            assertEquals("The name can only have letters, digits, dots (.), dashes (-) or underscores (_).", error["message"].asText())
            assertEquals("net.nemerosa.ontrack.graphql.support.MutationInputValidationException", error["exception"].asText())
            assertEquals("name", error["location"].asText())
            assertTrue(data["createProject"]["project"].isNullOrNullNode(), "Project not returned")
        }
    }

    @Test
    fun `Creating a project which already exist in get mode`() {
        asAdmin {
            val project = project()
            val data = run("""
                mutation CreateProject(${'$'}name: String!) {
                    createProjectOrGet(input: {name: ${'$'}name}) {
                        project {
                            id
                            name
                        }
                        errors {
                            message
                            exception
                            location
                        }
                    }
                }
            """, mapOf("name" to project.name))
            // Checks the errors
            assertNoUserError(data, "createProjectOrGet")
            val node = data["createProjectOrGet"]["project"]
            assertEquals(project.id(), node["id"].asInt())
            assertEquals(project.name, node["name"].asText())
        }
    }

    @Test
    fun `Creating a project which already exist in get mode with specific project access rights`() {
        project {
            withNoGrantViewToAll {
                asUserWithView {
                    val data = run(
                        """
                                mutation CreateProject(${'$'}name: String!) {
                                    createProjectOrGet(input: {name: ${'$'}name}) {
                                        project {
                                            id
                                            name
                                        }
                                        errors {
                                            message
                                            exception
                                            location
                                        }
                                    }
                                }
                            """, mapOf("name" to name)
                    )
                    // Checks the errors
                    assertNoUserError(data, "createProjectOrGet")
                    val node = data["createProjectOrGet"]["project"]
                    assertEquals(id(), node["id"].asInt())
                    assertEquals(name, node["name"].asText())
                }
            }
        }
    }

    @Test
    fun `Creating a project which already exists for another user`() {
        val project = project()
        withNoGrantViewToAll {
            asUserWith<ProjectCreation> {
                val data = run(
                    """
                        mutation CreateProject(${'$'}name: String!) {
                            createProjectOrGet(input: {name: ${'$'}name}) {
                                project {
                                    id
                                    name
                                }
                                errors {
                                    message
                                    exception
                                    location
                                }
                            }
                        }
                    """, mapOf("name" to project.name)
                )
                // Checks the errors
                val error = data["createProjectOrGet"]["errors"][0]
                assertEquals("Project name already exists: ${project.name}", error["message"].asText())
                assertEquals(
                    "net.nemerosa.ontrack.model.exceptions.ProjectNameAlreadyDefinedException",
                    error["exception"].asText()
                )
                assertTrue(data["createProjectOrGet"]["project"].isNullOrNullNode(), "Project not returned")
            }
        }
    }

    @Test
    fun `Creating a project in get mode`() {
        asUserWith<ProjectCreation> {
            val name = uid("P")
            val data = run(
                """
                    mutation CreateProject(${'$'}name: String!) {
                        createProjectOrGet(input: {name: ${'$'}name}) {
                            project {
                                id
                                name
                            }
                            errors {
                                message
                                exception
                                location
                            }
                        }
                    }
                """, mapOf("name" to name)
            )
            // Checks the errors
            assertNoUserError(data, "createProjectOrGet")
            val node = data["createProjectOrGet"]["project"]
            assertEquals(name, node["name"].asText())
        }
    }

    @Test
    fun `Updating a project's name`() {
        asAdmin {
            project {
                val newName = uid("P")
                val data = run("""
                    mutation UpdateProject(${'$'}name: String!) {
                        updateProject(input: {id: $id, name: ${'$'}name}) {
                            project {
                                id
                                name
                            }
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """, mapOf("name" to newName))
                // Checks for errors
                checkGraphQLUserErrors(data, "updateProject")
                // Checks the project has been created
                structureService.getProject(id).apply {
                    assertEquals(newName, name)
                }
                // Checks the data
                val project = data["updateProject"]["project"]
                assertTrue(project["id"].asInt() > 0, "ID is set")
                assertEquals(newName, project["name"].asText(), "Name is OK")
                assertTrue(data["updateProject"]["errors"].isNullOrNullNode(), "No error")
            }
        }
    }

    @Test
    fun `Updating a project's description`() {
        asAdmin {
            project {
                val newDescription = uid("P")
                val data = run("""
                    mutation UpdateProject(${'$'}description: String!) {
                        updateProject(input: {id: $id, description: ${'$'}description}) {
                            project {
                                id
                                name
                                description
                            }
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """, mapOf("description" to newDescription))
                // Checks for errors
                checkGraphQLUserErrors(data, "updateProject")
                // Checks the project has been created
                structureService.getProject(id).apply {
                    assertEquals(newDescription, description)
                }
                // Checks the data
                val project = data["updateProject"]["project"]
                assertTrue(project["id"].asInt() > 0, "ID is set")
                assertEquals(newDescription, project["description"].asText(), "Description is OK")
                assertTrue(data["updateProject"]["errors"].isNullOrNullNode(), "No error")
            }
        }
    }

    @Test
    fun `Updating a project's state`() {
        asAdmin {
            project {
                val data = run("""
                    mutation UpdateProject {
                        updateProject(input: {id: $id, disabled: true}) {
                            project {
                                id
                                name
                                disabled
                            }
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """)
                // Checks for errors
                checkGraphQLUserErrors(data, "updateProject")
                // Checks the project has been created
                structureService.getProject(id).apply {
                    assertTrue(isDisabled)
                }
                // Checks the data
                val project = data["updateProject"]["project"]
                assertTrue(project["id"].asInt() > 0, "ID is set")
                assertTrue(project["disabled"].asBoolean(), "State is OK")
                assertTrue(data["updateProject"]["errors"].isNullOrNullNode(), "No error")
            }
        }
    }

    @Test
    fun `Updating a project fails before of duplicated name`() {
        asAdmin {
            val existingName = project().name
            project {
                val data = run("""
                    mutation UpdateProject(${'$'}name: String!) {
                        updateProject(input: {id: $id, name: ${'$'}name}) {
                            project {
                                id
                                name
                            }
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """, mapOf("name" to existingName))
                // Checks the errors
                val error = data["updateProject"]["errors"][0]
                assertEquals("Project name already exists: $existingName", error["message"].asText())
                assertEquals("net.nemerosa.ontrack.model.exceptions.ProjectNameAlreadyDefinedException", error["exception"].asText())
                assertTrue(data["updateProject"]["project"].isNullOrNullNode(), "Project not returned")
            }
        }
    }

    @Test
    fun `Deleting a project`() {
        asAdmin {
            project {
                val data = run("""
                    mutation DeleteProject {
                        deleteProject(input: {id: $id}) {
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """)
                // Checks the project has been deleted
                assertNotPresent(structureService.findProjectByName(name))
                // Checks the data
                assertTrue(data["deleteProject"]["errors"].isNullOrNullNode(), "No error")
            }
        }
    }

    @Test
    fun `Disabling a project`() {
        asAdmin {
            project {
                val data = run("""
                    mutation DisableProject {
                        disableProject(input: {id: $id}) {
                            project {
                                disabled
                            }
                        }
                    }
                """)
                // Checks the project has been disabled
                assertTrue(structureService.getProject(id).isDisabled)
                // Checks the data
                assertTrue(data["disableProject"]["project"]["disabled"].asBoolean())
            }
        }
    }

    @Test
    fun `Enabling a project`() {
        asAdmin {
            project {
                structureService.disableProject(this)
                val data = run("""
                    mutation EnableProject {
                        enableProject(input: {id: $id}) {
                            project {
                                disabled
                            }
                        }
                    }
                """)
                // Checks the project has been enabled
                assertFalse(structureService.getProject(id).isDisabled)
                // Checks the data
                assertFalse(data["enableProject"]["project"]["disabled"].asBoolean())
            }
        }
    }

    @Test
    fun `Deleting a non existingproject`() {
        asAdmin {
            val project = project()
            structureService.deleteProject(project.id)
            val data = run("""
                    mutation DeleteProject {
                        deleteProject(input: {id: ${project.id}}) {
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """)
            // Checks the errors
            val error = data["deleteProject"]["errors"][0]
            assertEquals("Project ID not found: ${project.id}", error["message"].asText())
            assertEquals("net.nemerosa.ontrack.model.exceptions.ProjectNotFoundException", error["exception"].asText())
        }
    }

    @Test
    fun `Favourite branches for project`() {
        val account = doCreateAccount()
        project {
            branch {}
            val fav = branch {
                asConfigurableAccount(account).withView(this).execute {
                    branchFavouriteService.setBranchFavourite(this, true)
                }
            }
            // Gets the favourite branches in project
            val data = asConfigurableAccount(account).withView(this).call {
                run("""
                    {
                        projects(id: ${this.id}) {
                            branches(favourite: true) {
                                id
                            }
                        }
                    }
                """)
            }
            val branchIds: Set<Int> = data["projects"][0]["branches"].map { it["id"].asInt() }.toSet()
            assertEquals(
                setOf(fav.id()),
                branchIds
            )
        }
    }

    @Test
    fun `All projects`() {
        val p = doCreateProject()
        val data = run("{projects { id name }}")
        assertNotNull(data["projects"].find { it["name"].asText() == p.name }) {
            assertEquals(p.id(), it["id"].asInt())
        }
    }

    @Test
    fun `Project by name when not authorized must throw an authentication exception`() {
        // Creates a project
        val project = doCreateProject()
        // Looks for this project by name, with a not authorized user
        assertFailsWith(AccessDeniedException::class, "Access denied") {
            withNoGrantViewToAll {
                asUser().call {
                    run("""{
                |  projects(name: "${project.name}") {
                |    id
                |  }
                |}""".trimMargin())
                }
            }
        }
    }

    @Test
    fun `Last promoted build`() {
        // Creating a promotion level
        val pl = doCreatePromotionLevel()
        // Creating a first promoted build
        val build1 = doCreateBuild(pl.branch, NameDescription.nd("1", ""))
        doPromote(build1, pl, "One")
        // Creating a second promoted build
        val build2 = doCreateBuild(pl.branch, NameDescription.nd("2", ""))
        doPromote(build2, pl, "Two")
        // Run a GraphQL query at project level and gets the last promotion run
        val data = run("""{
            |   projects(id: ${pl.project.id}) {
            |      branches {
            |          promotionLevels {
            |              name
            |              promotionRuns(first: 1) {
            |                build {
            |                  name
            |                }
            |              }
            |          }
            |      }
            |   }
            |}
        """.trimMargin())
        // Checks that the build associated with the promotion is the last one
        val plNode = data["projects"][0]["branches"][0]["promotionLevels"][0]
        assertEquals(pl.name, plNode["name"].asText())
        val runNodes = plNode["promotionRuns"]
        assertEquals(1, runNodes.size())
        val build = runNodes[0]["build"]
        assertEquals(build2.name, build["name"].asText())
    }

    @Test
    fun `Validation run statuses for a run for a validation stamp`() {
        project {
            branch {
                val vs = validationStamp()
                build("1") {
                    validate(vs, ValidationRunStatusID.STATUS_FAILED, description = "Validation failed").apply {
                        validationStatus(ValidationRunStatusID.STATUS_INVESTIGATING, "Investigating")
                        validationStatus(ValidationRunStatusID.STATUS_EXPLAINED, "Explained")
                    }
                    val data = run("""{
                        projects (id: ${project.id}) {
                            branches (name: "${branch.name}") {
                                validationStamps {
                                    name
                                    validationRuns {
                                        validationRunStatuses {
                                            statusID {
                                                id
                                            }
                                            description
                                        }
                                    }
                                }
                            }
                        }
                    }""")
                    val validationRunStatuses =
                        data["projects"][0]["branches"][0]["validationStamps"][0]["validationRuns"][0]["validationRunStatuses"]
                    assertEquals(
                        listOf("EXPLAINED", "INVESTIGATING", "FAILED"),
                        validationRunStatuses.map { it["statusID"]["id"].asText() }
                    )
                    assertEquals(
                        listOf("Explained", "Investigating", "Validation failed"),
                        validationRunStatuses.map { it["description"].asText() }
                    )
                }
            }
        }
    }

}