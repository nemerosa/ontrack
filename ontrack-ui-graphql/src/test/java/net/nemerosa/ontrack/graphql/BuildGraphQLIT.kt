package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.api.support.TestSimpleProperty
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType
import net.nemerosa.ontrack.json.*
import net.nemerosa.ontrack.model.security.BuildCreate
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.RunInfoInput
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.assertJsonNotNull
import net.nemerosa.ontrack.test.assertJsonNull
import org.junit.jupiter.api.Test
import org.springframework.graphql.execution.ErrorType
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Integration tests around the `builds` root query.
 */
class BuildGraphQLIT : AbstractQLKTITSupport() {

    @Test
    fun `Build creation`() {
        val branch = doCreateBranch()
        val build = asUser().withProjectFunction(branch, BuildCreate::class.java).call {
            structureService.newBuild(
                    Build.of(
                            branch,
                            nd("1", ""),
                            Signature.of(
                                    LocalDateTime.of(2016, 11, 25, 14, 43),
                                    "test"
                            )
                    )
            )
        }

        run("""{builds (id: ${build.id}) { creation { user time } } }""") { data ->
            val creation = data.path("builds").first().path("creation")
            assertEquals("test", creation.getRequiredTextField("user"))
            assertEquals("2016-11-25T14:43:00", creation.getRequiredTextField("time"))
        }
    }

    @Test
    fun `Build property by name`() {
        val build = doCreateBuild()
        setProperty(build, TestSimplePropertyType::class.java, TestSimpleProperty("value 1"))
        run("""{
            builds(id: ${build.id}) {
                testSimpleProperty { 
                    type { 
                        typeName 
                        name
                        description
                    }
                    value
                    editable
                }
            }
        }""") { data ->
            val p = data.path("builds").first().path("testSimpleProperty")
            assertEquals("net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType", p.path("type").path("typeName").asText())
            assertEquals("Simple value", p.path("type").path("name").asText())
            assertEquals("Value.", p.path("type").path("description").asText())
            assertEquals("value 1", p.path("value").path("value").asText())
            assertEquals(false, p.path("editable").asBoolean())
        }
    }

    @Test
    fun `Build property by list`() {
        val build = doCreateBuild()
        setProperty(build, TestSimplePropertyType::class.java, TestSimpleProperty("value 2"))
        run("""{
            builds(id: ${build.id}) {
                properties { 
                    type { 
                        typeName 
                        name
                        description
                    }
                    value
                    editable
                }
            }
        }""") { data ->
            var p = data.path("builds").first().path("properties").find {
                it.path("type").path("name").asText() == "Simple value"
            } ?: fail("Cannot find property")
            assertEquals("net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType", p.path("type").path("typeName").asText())
            assertEquals("Simple value", p.path("type").path("name").asText())
            assertEquals("Value.", p.path("type").path("description").asText())
            assertEquals("value 2", p.path("value").path("value").asText())
            assertEquals(false, p.path("editable").asBoolean())

            p = data.path("builds").first().path("properties").find {
                it.path("type").path("name").asText() == "Configuration value"
            } ?: fail("Cannot find property")
            assertEquals("net.nemerosa.ontrack.extension.api.support.TestPropertyType", p.path("type").path("typeName").asText())
            assertEquals("Configuration value", p.path("type").path("name").asText())
            assertEquals("Value.", p.path("type").path("description").asText())
            assertJsonNull(p.path("type").path("value"))
            assertEquals(false, p.path("editable").asBoolean())
        }
    }

    @Test
    fun `Build property filtered by type`() {
        val build = doCreateBuild()
        setProperty(build, TestSimplePropertyType::class.java, TestSimpleProperty("value 2"))
        run("""{
            builds(id: ${build.id}) {
                properties(type: "net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType") { 
                    type { 
                        typeName 
                        name
                        description
                    }
                    value
                    editable
                }
            }
        }""") { data ->
            val properties = data.path("builds").first().path("properties")
            assertEquals(1, properties.size())
            val p = properties.first()
            assertEquals("net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType", p.path("type").path("typeName").asText())
            assertEquals("Simple value", p.path("type").path("name").asText())
            assertEquals("Value.", p.path("type").path("description").asText())
            assertEquals("value 2", p.path("value").path("value").asText())
            assertEquals(false, p.path("editable").asBoolean())
        }
    }

    @Test
    fun `Build property filtered by value`() {
        val build = doCreateBuild()
        setProperty(build, TestSimplePropertyType::class.java, TestSimpleProperty("value 2"))
        run("""{
            builds(id: ${build.id}) {
                properties(hasValue: true) { 
                    type { 
                        typeName 
                        name
                        description
                    }
                    value
                    editable
                }
            }
        }""") { data ->
            val properties = data.path("builds").first().path("properties")
            assertEquals(1, properties.size())
            val p = properties.first()
            assertEquals("net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType", p.path("type").path("typeName").asText())
            assertEquals("Simple value", p.path("type").path("name").asText())
            assertEquals("Value.", p.path("type").path("description").asText())
            assertEquals("value 2", p.path("value").path("value").asText())
            assertEquals(false, p.path("editable").asBoolean())
        }
    }

    @Test
    fun `By branch not found`() {
        project {
            val name = uid("B")
            run(
                    """{
                    builds(project: "${project.name}", branch: "$name") {
                        name
                    }
                }"""
            ) { data ->
                assertTrue(
                        data["builds"].isEmpty,
                        "No build is returned"
                )
            }
        }
    }

    @Test
    fun `By branch`() {
        val build = doCreateBuild()

        run("""{
            builds(project: "${build.project.name}", branch: "${build.branch.name}") {
                id
            }
        }""") { data ->
            assertEquals(build.id(), data.path("builds").first().getIntField("id"))
        }
    }

    @Test
    fun `By project not found`() {
        val name = uid("P")
        run(
                """{
                    builds(project: "$name") {
                        name
                    }
                }"""
        ) { data ->
            assertTrue(data["builds"].isEmpty, "No build is returned")
        }
    }

    @Test
    fun `By project`() {
        val build = doCreateBuild()

        run("""{
            builds(project: "${build.project.name}") {
                id
            }
        }""") { data ->
            assertEquals(build.id(), data.path("builds").first().getIntField("id"))
        }
    }

    @Test
    fun `No argument means no result`() {
        doCreateBuild()
        run("""{
            builds {
                id
            }
        }""") { data ->
            assertTrue(data.path("builds").isEmpty)
        }
    }

    @Test
    fun `Branch filter`() {
        // Builds
        val branch = doCreateBranch()
        val build1 = doCreateBuild(branch, nd("1", ""))
        doCreateBuild(branch, nd("2", ""))
        val pl = doCreatePromotionLevel(branch, nd("PL", ""))
        doPromote(build1, pl, "")
        // Query
        run("""{
            builds(
                    project: "${branch.project.name}", 
                    branch: "${branch.name}", 
                    buildBranchFilter: {withPromotionLevel: "PL"}) {
                id
            }
        }""") { data ->
            val builds = data.path("builds")
            assertEquals(1, builds.size())
            assertEquals(build1.id(), builds.first().getIntField("id"))
        }
    }

    @Test
    fun `Build filter with promotion since promotion when no promotion is available`() {
        // Branch
        val branch = doCreateBranch()
        // A few builds without promotions
        (1..4).forEach {
            doCreateBuild(branch, nd(it.toString(), ""))
        }
        // Query
        run(""" {
          builds(project: "${branch.project.name}", branch: "${branch.name}", buildBranchFilter: {count: 100, withPromotionLevel: "BRONZE", sincePromotionLevel: "SILVER"}) {
            id
          }
        }""") { data ->
            // We should not have any build
            assertEquals(0, data.path("builds").size())
        }
    }

    @Test
    fun `Branch filter requires a branch`() {
        // Builds
        val branch = doCreateBranch()
        val build1 = doCreateBuild(branch, nd("1", ""))
        doCreateBuild(branch, nd("2", ""))
        val pl = doCreatePromotionLevel(branch, nd("PL", ""))
        doPromote(build1, pl, "")
        // Query
        runWithError(
                """{
                    builds(buildBranchFilter: {withPromotionLevel: "PL"}) {
                        id
                    }
                }""",
                errorClassification = ErrorType.BAD_REQUEST
        )
    }

    @Test
    fun `Project filter requires a project`() {
        // Builds
        doCreateBuild()
        // Query
        runWithError(
                """{
                builds( 
                        buildProjectFilter: {promotionName: "PL"}) {
                    id
                }
            }""",
                errorClassification = ErrorType.BAD_REQUEST
        )
    }

    @Test
    fun `Project filter`() {
        // Builds
        val project = doCreateProject()
        val branch1 = doCreateBranch(project, nd("1.0", ""))
        doCreateBuild(branch1, nd("1.0.0", ""))
        val branch2 = doCreateBranch(project, nd("2.0", ""))
        doCreateBuild(branch2, nd("2.0.0", ""))
        doCreateBuild(branch2, nd("2.0.1", ""))
        // Query
        run("""{
            builds( 
                    project: "${project.name}",
                    buildProjectFilter: {branchName: "2.0"}) {
                name
            }
        }""") { data ->
            assertEquals(2, data.path("builds").size())
            assertEquals("2.0.1", data.path("builds").get(0).getTextField("name"))
            assertEquals("2.0.0", data.path("builds").get(1).getTextField("name"))
        }
    }

    @Test
    fun `Creating a build from a branch ID`() {
        asAdmin {
            project project@{
                branch branch@{
                    val data = run("""
                        mutation {
                            createBuild(input: {branchId: ${this@branch.id}, name: "1"}) {
                                build {
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
                    // Checks the build has been created
                    assertNotNull(
                            structureService.findBuildByName(this@project.name, this@branch.name, "1").getOrNull(),
                            "Build has been created")
                    // Checks the data
                    val build = data["createBuild"]["build"]
                    assertTrue(build["id"].asInt() > 0, "ID is set")
                    assertEquals("1", build["name"].asText(), "Name is OK")
                    assertTrue(data["createBuild"]["errors"].isNullOrNullNode(), "No error")
                }
            }
        }
    }

    @Test
    fun `Creating a build with some run info`() {
        asAdmin {
            project project@{
                branch branch@{
                    val runInfo = RunInfoInput(runTime = 27)
                    val data = run("""
                        mutation CreateBuild(${"$"}runInfo: RunInfoInput) {
                            createBuild(input: {branchId: ${this@branch.id}, name: "1", runInfo: ${"$"}runInfo}) {
                                build {
                                    id
                                    name
                                    runInfo {
                                        runTime
                                    }
                                }
                                errors {
                                    message
                                    exception
                                }
                            }
                        }
                    """, mapOf(
                            "runInfo" to runInfo.asJson().toJsonMap()
                    ))
                    // Checks the build has been created
                    assertNotNull(
                            structureService.findBuildByName(this@project.name, this@branch.name, "1").getOrNull(),
                            "Build has been created")
                    // Checks the data
                    val node = assertNoUserError(data, "createBuild")
                    val build = node["build"]
                    assertTrue(build["id"].asInt() > 0, "ID is set")
                    assertEquals("1", build["name"].asText(), "Name is OK")
                    // Run info
                    assertEquals(27, build.path("runInfo").path("runTime").asInt())
                }
            }
        }
    }

    @Test
    fun `Creating a build from a branch ID with invalid name`() {
        asAdmin {
            project project@{
                branch branch@{
                    val data = run("""
                        mutation {
                            createBuild(input: {branchId: ${this@branch.id}, name: "1 0"}) {
                                build {
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
                    val error = data["createBuild"]["errors"][0]
                    assertEquals("The name can only have letters, digits, dots (.), dashes (-) or underscores (_).", error["message"].asText())
                    assertEquals("net.nemerosa.ontrack.graphql.support.MutationInputValidationException", error["exception"].asText())
                    assertTrue(data["createBuild"]["build"].isNullOrNullNode(), "Build not returned")
                }
            }
        }
    }

    @Test
    fun `Creating a build from a branch ID with existing name`() {
        asAdmin {
            project project@{
                branch branch@{
                    build(name = "1")
                    val data = run("""
                        mutation {
                            createBuild(input: {branchId: ${this@branch.id}, name: "1"}) {
                                build {
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
                    val error = data["createBuild"]["errors"][0]
                    assertEquals("Build name already exists: 1", error["message"].asText())
                    assertEquals("net.nemerosa.ontrack.model.exceptions.BuildNameAlreadyDefinedException", error["exception"].asText())
                    assertTrue(data["createBuild"]["build"].isNullOrNullNode(), "Build not returned")
                }
            }
        }
    }

    @Test
    fun `Creating a build from a project ID and branch name`() {
        asAdmin {
            project project@{
                branch branch@{
                    val data = run("""
                        mutation {
                            createBuild(input: {projectId: ${this@project.id}, branchName: "${this@branch.name}", name: "1"}) {
                                build {
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
                    // Checks the build has been created
                    assertNotNull(
                            structureService.findBuildByName(this@project.name, this@branch.name, "1").getOrNull(),
                            "Build has been created")
                    // Checks the data
                    val build = data["createBuild"]["build"]
                    assertTrue(build["id"].asInt() > 0, "ID is set")
                    assertEquals("1", build["name"].asText(), "Name is OK")
                    assertTrue(data["createBuild"]["errors"].isNullOrNullNode(), "No error")
                }
            }
        }
    }

    @Test
    fun `Creating a build from a project name and branch name`() {
        asAdmin {
            project project@{
                branch branch@{
                    val data = run("""
                        mutation {
                            createBuild(input: {projectName: "${this@project.name}", branchName: "${this@branch.name}", name: "1"}) {
                                build {
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
                    // Checks the build has been created
                    assertNotNull(
                            structureService.findBuildByName(this@project.name, this@branch.name, "1").getOrNull(),
                            "Build has been created")
                    // Checks the data
                    val build = data["createBuild"]["build"]
                    assertTrue(build["id"].asInt() > 0, "ID is set")
                    assertEquals("1", build["name"].asText(), "Name is OK")
                    assertTrue(data["createBuild"]["errors"].isNullOrNullNode(), "No error")
                }
            }
        }
    }

    @Test
    fun `Creating a build from a project ID and missing branch name`() {
        asAdmin {
            project project@{
                branch branch@{
                    build(name = "1")
                    val data = run("""
                        mutation {
                            createBuild(input: {projectId: ${this@project.id}, name: "1"}) {
                                build {
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
                    val error = data["createBuild"]["errors"][0]
                    assertEquals("branchName is required if branchId is not provided", error["message"].asText())
                    assertEquals("net.nemerosa.ontrack.graphql.schema.BuildInputMismatchException", error["exception"].asText())
                    assertTrue(data["createBuild"]["build"].isNullOrNullNode(), "Build not returned")
                }
            }
        }
    }

    @Test
    fun `Creating a build from a branch name and missing project information`() {
        asAdmin {
            project project@{
                branch branch@{
                    build(name = "1")
                    val data = run("""
                        mutation {
                            createBuild(input: {branchName: "${this@branch.name}", name: "1"}) {
                                build {
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
                    val error = data["createBuild"]["errors"][0]
                    assertEquals("When using branchName, projectName is required if projectId is not provided", error["message"].asText())
                    assertEquals("net.nemerosa.ontrack.graphql.schema.BuildInputMismatchException", error["exception"].asText())
                    assertTrue(data["createBuild"]["build"].isNullOrNullNode(), "Build not returned")
                }
            }
        }
    }

    @Test
    fun `Creating a build from a branch ID and superfluous branch name`() {
        asAdmin {
            project project@{
                branch branch@{
                    build(name = "1")
                    val data = run("""
                        mutation {
                            createBuild(input: {branchId: ${this@branch.id}, branchName: "${this@branch.name}", name: "1"}) {
                                build {
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
                    val error = data["createBuild"]["errors"][0]
                    assertEquals("Since branchId is provided, branchName is not required.", error["message"].asText())
                    assertEquals("net.nemerosa.ontrack.graphql.schema.BuildInputMismatchException", error["exception"].asText())
                    assertTrue(data["createBuild"]["build"].isNullOrNullNode(), "Build not returned")
                }
            }
        }
    }

    @Test
    fun `Creating a build from a branch ID and superfluous project name`() {
        asAdmin {
            project project@{
                branch branch@{
                    build(name = "1")
                    val data = run("""
                        mutation {
                            createBuild(input: {branchId: ${this@branch.id}, projectName: "${this@project.name}", name: "1"}) {
                                build {
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
                    val error = data["createBuild"]["errors"][0]
                    assertEquals("Since branchId is provided, projectName is not required.", error["message"].asText())
                    assertEquals("net.nemerosa.ontrack.graphql.schema.BuildInputMismatchException", error["exception"].asText())
                    assertTrue(data["createBuild"]["build"].isNullOrNullNode(), "Build not returned")
                }
            }
        }
    }

    @Test
    fun `Creating a build from a branch ID and superfluous project ID`() {
        asAdmin {
            project project@{
                branch branch@{
                    build(name = "1")
                    val data = run("""
                        mutation {
                            createBuild(input: {branchId: ${this@branch.id}, projectId: ${this@project.id}, name: "1"}) {
                                build {
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
                    val error = data["createBuild"]["errors"][0]
                    assertEquals("Since branchId is provided, projectId is not required.", error["message"].asText())
                    assertEquals("net.nemerosa.ontrack.graphql.schema.BuildInputMismatchException", error["exception"].asText())
                    assertTrue(data["createBuild"]["build"].isNullOrNullNode(), "Build not returned")
                }
            }
        }
    }

    @Test
    fun `Creating a build from a project ID, branch name and superfluous project name`() {
        asAdmin {
            project project@{
                branch branch@{
                    build(name = "1")
                    val data = run("""
                        mutation {
                            createBuild(input: {branchName: "${this@branch.name}", projectId: ${this@project.id}, projectName: "${this@project.name}", name: "1"}) {
                                build {
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
                    val error = data["createBuild"]["errors"][0]
                    assertEquals("Since projectId is provided, projectName is not required.", error["message"].asText())
                    assertEquals("net.nemerosa.ontrack.graphql.schema.BuildInputMismatchException", error["exception"].asText())
                    assertTrue(data["createBuild"]["build"].isNullOrNullNode(), "Build not returned")
                }
            }
        }
    }

    @Test
    fun `Creating a build in get mode from a branch ID`() {
        asAdmin {
            project project@{
                branch branch@{
                    val data = run("""
                        mutation {
                            createBuildOrGet(input: {branchId: ${this@branch.id}, name: "1"}) {
                                build {
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
                    // Checks the build has been created
                    assertNotNull(
                            structureService.findBuildByName(this@project.name, this@branch.name, "1").getOrNull(),
                            "Build has been created")
                    // Checks the data
                    val build = data["createBuildOrGet"]["build"]
                    assertTrue(build["id"].asInt() > 0, "ID is set")
                    assertEquals("1", build["name"].asText(), "Name is OK")
                    assertTrue(data["createBuildOrGet"]["errors"].isNullOrNullNode(), "No error")
                }
            }
        }
    }

    @Test
    fun `Creating a build in get mode from a branch name and project name`() {
        asAdmin {
            project project@{
                branch branch@{
                    val data = run("""
                        mutation {
                            createBuildOrGet(input: {projectName: "${this@project.name}", branchName: "${this@branch.name}", name: "1"}) {
                                build {
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
                    // Checks the build has been created
                    assertNotNull(
                            structureService.findBuildByName(this@project.name, this@branch.name, "1").getOrNull(),
                            "Build has been created")
                    // Checks the data
                    val build = data["createBuildOrGet"]["build"]
                    assertTrue(build["id"].asInt() > 0, "ID is set")
                    assertEquals("1", build["name"].asText(), "Name is OK")
                    assertTrue(data["createBuildOrGet"]["errors"].isNullOrNullNode(), "No error")
                }
            }
        }
    }

    @Test
    fun `Creating a build in get mode for an existing build from a branch ID`() {
        asAdmin {
            project project@{
                branch branch@{
                    val existing = build(name = "1")
                    val data = run("""
                        mutation {
                            createBuildOrGet(input: {branchId: ${this@branch.id}, name: "1"}) {
                                build {
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
                    // Checks the build has been created
                    assertNotNull(
                            structureService.findBuildByName(this@project.name, this@branch.name, "1").getOrNull(),
                            "Build has been created")
                    // Checks the data
                    val build = data["createBuildOrGet"]["build"]
                    assertEquals(existing.id(), build["id"].asInt(), "ID is the same")
                    assertEquals("1", build["name"].asText(), "Name is OK")
                    assertTrue(data["createBuildOrGet"]["errors"].isNullOrNullNode(), "No error")
                }
            }
        }
    }

    @Test
    fun `Creating a build in get mode for an existing build from a branch name and project name`() {
        asAdmin {
            project project@{
                branch branch@{
                    val existing = build(name = "1")
                    val data = run("""
                        mutation {
                            createBuildOrGet(input: {projectName: "${this@project.name}", branchName: "${this@branch.name}", name: "1"}) {
                                build {
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
                    // Checks the build has been created
                    assertNotNull(
                            structureService.findBuildByName(this@project.name, this@branch.name, "1").getOrNull(),
                            "Build has been created")
                    // Checks the data
                    val build = data["createBuildOrGet"]["build"]
                    assertEquals(existing.id(), build["id"].asInt(), "ID is the same")
                    assertEquals("1", build["name"].asText(), "Name is OK")
                    assertTrue(data["createBuildOrGet"]["errors"].isNullOrNullNode(), "No error")
                }
            }
        }
    }

    @Test
    fun `Filtered build links`() {
        // Reference project with two builds to reference
        val ref1 = project {
            branch("maintenance") {
                build("1.0")
            }
            branch("master") {
                build("2.0")
            }
        }
        // Other reference project with one build
        val ref2 = project {
            branch("master") {
                build("3.0")
            }
        }
        // Parent build
        project {
            branch {
                build {
                    // Links to all the builds above
                    linkTo(ref1, "1.0")
                    linkTo(ref1, "2.0")
                    linkTo(ref2, "3.0")

                    // No filter
                    run("""{
                        builds(id: $id) {
                            using {
                                pageItems {
                                    name
                                }
                            }
                        }
                    }"""
                    ).run {
                        this["builds"][0]["using"]["pageItems"].map { it["name"].asText() }.toSet()
                    }.run {
                        assertEquals(
                                setOf("1.0", "2.0", "3.0"),
                                this
                        )
                    }

                    // Filter by project
                    run("""{
                        builds(id: $id) {
                            using(project: "${ref1.name}") {
                                pageItems {
                                    name
                                }
                            }
                        }
                    }"""
                    ).run {
                        this["builds"][0]["using"]["pageItems"].map { it["name"].asText() }.toSet()
                    }.run {
                        assertEquals(
                                setOf("1.0", "2.0"),
                                this
                        )
                    }

                    // Filter by branch
                    run("""{
                        builds(id: $id) {
                            using(project: "${ref1.name}", branch: "master") {
                                pageItems {
                                    name
                                }
                            }
                        }
                    }"""
                    ).run {
                        this["builds"][0]["using"]["pageItems"].map { it["name"].asText() }.toSet()
                    }.run {
                        assertEquals(
                                setOf("2.0"),
                                this
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Filtered build origin links`() {
        // Target build
        val build = project<Build> {
            branch<Build> {
                build()
            }
        }
        // Reference project with two builds to reference from
        val ref1 = project {
            branch("maintenance") {
                build("1.0") {
                    linkTo(build.project, build.name)
                }
            }
            branch("master") {
                build("2.0") {
                    linkTo(build.project, build.name)
                }
            }
        }
        // Other reference project with one build
        project {
            branch("master") {
                build("3.0") {
                    linkTo(build.project, build.name)
                }
            }
        }

        build.apply {
            // No filter, all of them
            run("""{
                        builds(id: $id) {
                            usedBy {
                                pageItems {
                                    name
                                }
                            }
                        }
                    }"""
            ).run {
                this["builds"][0]["usedBy"]["pageItems"].map { it["name"].asText() }.toSet()
            }.run {
                assertEquals(
                        setOf("1.0", "2.0", "3.0"),
                        this
                )
            }
            // No filter, first one only
            run("""{
                        builds(id: $id) {
                            usedBy(size: 1) {
                                pageItems {
                                    name
                                }
                            }
                        }
                    }"""
            ).run {
                this["builds"][0]["usedBy"]["pageItems"].map { it["name"].asText() }.toSet()
            }.run {
                assertEquals(
                        setOf("3.0"),
                        this
                )
            }
            // Project restriction, all of them
            run("""{
                        builds(id: $id) {
                            usedBy(project: "${ref1.name}") {
                                pageItems {
                                    name
                                }
                            }
                        }
                    }"""
            ).run {
                this["builds"][0]["usedBy"]["pageItems"].map { it["name"].asText() }.toSet()
            }.run {
                assertEquals(
                        setOf("1.0", "2.0"),
                        this
                )
            }
            // Project restriction, first one
            run("""{
                        builds(id: $id) {
                            usedBy(project: "${ref1.name}", size: 1) {
                                pageItems {
                                    name
                                }
                            }
                        }
                    }"""
            ).run {
                this["builds"][0]["usedBy"]["pageItems"].map { it["name"].asText() }.toSet()
            }.run {
                assertEquals(
                        setOf("2.0"),
                        this
                )
            }
            // Branch restriction
            run("""{
                        builds(id: $id) {
                            usedBy(project: "${ref1.name}", branch: "master") {
                                pageItems {
                                    name
                                }
                            }
                        }
                    }"""
            ).run {
                this["builds"][0]["usedBy"]["pageItems"].map { it["name"].asText() }.toSet()
            }.run {
                assertEquals(
                        setOf("2.0"),
                        this
                )
            }
        }
    }

    @Test
    fun `Build links are empty by default`() {
        val build = doCreateBuild()

        val data = run("""{
            builds(id: ${build.id}) {
                using {
                    pageItems {
                        name
                    }
                }
                usedBy {
                    pageItems {
                        name
                    }
                }
            }
        }""")

        val b = data["builds"].first()
        assertNotNull(b["using"]["pageItems"]) {
            assertTrue(it.size() == 0)
        }
        assertNotNull(b["usedBy"]["pageItems"]) {
            assertTrue(it.size() == 0)
        }
    }

    @Test
    fun `Build links`() {
        val build = doCreateBuild()
        val targetBuild = doCreateBuild()

        asAdmin().execute {
            structureService.addBuildLink(build, targetBuild)
        }

        val data = run("""{
            builds(id: ${build.id}) {
                using {
                    pageItems {
                        name
                        branch {
                            name
                            project {
                                name
                            }
                        }
                    }
                }
            }
        }""")

        val links = data["builds"].first()["using"]["pageItems"]
        assertNotNull(links) {
            assertEquals(1, it.size())
            val link = it.first()
            assertEquals(targetBuild.name, link["name"].asText())
            assertEquals(targetBuild.branch.name, link["branch"]["name"].asText())
            assertEquals(targetBuild.branch.project.name, link["branch"]["project"]["name"].asText())
        }

    }

    @Test
    fun `Following build links TO`() {
        // Three builds
        val a = doCreateBuild()
        val b = doCreateBuild()
        val c = doCreateBuild()
        // Links: a -> b -> c
        asAdmin().execute {
            structureService.addBuildLink(a, b)
            structureService.addBuildLink(b, c)
        }
        // Query build links of "b" TO
        val data = run("""{
            builds(id: ${b.id}) {
                using {
                    pageItems {
                        id
                        name
                    }
                }
            }
        }""")
        // Checks the result
        val links = data["builds"].first()["using"]["pageItems"]
        assertNotNull(links) {
            assertEquals(1, it.size())
            val link = it.first()
            assertEquals(c.name, link["name"].asText())
            assertEquals(c.id(), link["id"].asInt())
        }
    }

    @Test
    fun `Following build links FROM`() {
        // Three builds
        val a = doCreateBuild()
        val b = doCreateBuild()
        val c = doCreateBuild()
        // Links: a -> b -> c
        asAdmin().execute {
            structureService.addBuildLink(a, b)
            structureService.addBuildLink(b, c)
        }
        // Query build links of "b" FROM
        val data = withGrantViewToAll {
            run("""{
                builds(id: ${b.id}) {
                    usedBy {
                        pageItems {
                            id
                            name
                        }
                    }
                }
            }""")
        }
        // Checks the result
        val links = data["builds"].first()["usedBy"]["pageItems"]
        assertNotNull(links) {
            assertEquals(1, it.size())
            val link = it.first()
            assertEquals(a.name, link["name"].asText())
            assertEquals(a.id(), link["id"].asInt())
        }
    }

    @Test
    fun `Build using dependencies`() {
        val dep1 = project<Build> {
            branch<Build> {
                val pl = promotionLevel("IRON")
                build {
                    promote(pl)
                }
            }
        }
        val dep2 = project<Build> {
            branch<Build> {
                build()
            }
        }
        // Source build
        project {
            branch {
                build {
                    // Creates links
                    linkTo(dep1)
                    linkTo(dep2)
                    // Looks for dependencies
                    val data = asUser().withView(this).call {
                        run("""
                            {
                                builds(id: $id) {
                                    name
                                    using {
                                        pageItems {
                                            id
                                        }
                                    }
                                }
                            }
                        """.trimIndent())
                    }
                    // Dependencies Ids
                    val dependencies = data["builds"][0]["using"]["pageItems"].map { it["id"].asInt() }
                    assertEquals(
                            setOf(dep1.id(), dep2.id()),
                            dependencies.toSet()
                    )
                }
            }
        }
    }

    @Test
    fun `Promotion runs when promotion does not exist`() {
        // Creates a build
        val build = doCreateBuild()
        // Looks for promotion runs
        val data = asUser().withView(build).call {
            run("""
                {
                    builds(id: ${build.id}) {
                        name
                        promotionRuns(promotion: "PLATINUM") {
                          creation {
                            time
                          }
                        }
                    }
                }
            """.trimIndent())
        }
        // Checks the build
        val b = data["builds"][0]
        assertEquals(build.name, b["name"].asText())
        // Checks that there is no promotion run (but the query did not fail!)
        assertEquals(0, b["promotionRuns"].size())
    }

    @Test
    fun `Validations when validation stamp does not exist`() {
        // Creates a build
        val build = doCreateBuild()
        // Looks for validations
        val data = asUser().withView(build).call {
            run("""
                {
                    builds(id: ${build.id}) {
                        name
                        validations(validationStamp: "VS") {
                          validationRuns(count: 1) {
                            creation {
                              time
                            }
                          }
                        }
                    }
                }
            """.trimIndent())
        }
        // Checks the build
        val b = data["builds"][0]
        assertEquals(build.name, b["name"].asText())
        // Checks that there is no validation run (but the query did not fail!)
        assertEquals(0, b["validations"].size())
    }

    @Test
    fun `Validation runs filtered by validation stamp`() {
        project {
            branch {
                val stamps = (1..3).map {
                    validationStamp(name = "VS$it")
                }
                build {
                    stamps.forEach { vs ->
                        validate(vs)
                    }
                    // Validation runs for this build, filtered on first validation stamp
                    val data = asUserWithView {
                        run("""{
                            builds(id: $id) {
                                validationRuns(validationStamp: "VS1") {
                                    id
                                }
                            }
                        }""")
                    }
                    // Checks the validation runs
                    val runs = data["builds"][0]["validationRuns"]
                    assertEquals(1, runs.size())
                }
            }
        }
    }

    @Test
    fun `Validation runs filtered by validation stamp using a regular expression`() {
        project {
            branch {
                val stamps = (1..3).map {
                    validationStamp(name = "VS$it")
                }
                build {
                    stamps.forEach { vs ->
                        validate(vs)
                    }
                    // Validation runs for this build, filtered on first validation stamp
                    val data = asUserWithView {
                        run("""{
                            builds(id: $id) {
                                validationRuns(validationStamp: "VS(1|2)") {
                                    id
                                }
                            }
                        }""")
                    }
                    // Checks the validation runs
                    val runs = data["builds"][0]["validationRuns"]
                    assertEquals(2, runs.size())
                }
            }
        }
    }

    @Test
    fun `Validation runs sorted by decreasing run time`() {
        project {
            branch {
                val stamps = (1..3).map {
                    validationStamp(name = "VS$it")
                }
                build {
                    // Creating validation runs with decreasing run times
                    val runs = stamps.mapIndexed { index, vs ->
                        validate(vs, duration = 100 - 10 * index)
                    }
                    // Validation runs for this build, ordered by decreasing run time
                    asUserWithView {
                        run("""{
                            builds(id: $id) {
                                validationRunsPaginated(sortingMode: RUN_TIME) {
                                    pageItems {
                                        id
                                        runInfo {
                                            runTime
                                        }
                                    }
                                }
                            }
                        }""") { data ->
                            val build = data.path("builds").path(0)
                            val actualRunIds = build.path("validationRunsPaginated").path("pageItems").map {
                                it.getRequiredIntField("id")
                            }
                            val runTimes = build.path("validationRunsPaginated").path("pageItems").map {
                                it.path("runInfo").getRequiredIntField("runTime")
                            }
                            val expectedRunIds = runs.map { it.id() }
                            assertEquals(
                                    expectedRunIds, actualRunIds,
                                    "Runs are sorted"
                            )
                            assertEquals(
                                    listOf(100, 90, 80),
                                    runTimes,
                                    "Correctly sorted run times"
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Build by name`() {
        project {
            branch {
                val builds = (1..10).map { build("$it") }
                // Gets all builds by name, one by one
                builds.forEach { build ->
                    val data = asUserWithView {
                        run("""{
                            builds(project: "${build.branch.project.name}", branch: "${build.branch.name}", name: "${build.name}") {
                                id
                            }
                        }""")
                    }
                    val id = data["builds"][0]["id"].asInt()
                    assertEquals(build.id(), id)
                }
                // Looks for a non existing build
                val data = asUserWithView {
                    run("""{
                        builds(project: "${project.name}", branch: "$name", name: "11") {
                            id
                        }
                    }""")
                }
                val list = data["builds"]
                assertEquals(0, list.size())
            }
        }
    }

    @Test
    fun `Previous and next builds`() {

        fun doTest(branch: Branch, buildName: String, previousName: String?, nextName: String?) {
            run("""
                {
                    builds(project: "${branch.project.name}", branch: "${branch.name}", name: "$buildName") {
                        name
                        previousBuild { name }
                        nextBuild { name }
                    }
                }
            """) { data ->
                val build = data.path("builds").path(0)
                assertEquals(buildName, build.path("name").asText(), "Build found")
                if (previousName != null) {
                    assertJsonNotNull(build.path("previousBuild"), "Previous build") {
                        assertEquals(previousName, path("name").asText(), "Previous build")
                    }
                } else {
                    assertJsonNull(build.path("previousBuild"), "No previous build")
                }
                if (nextName != null) {
                    assertJsonNotNull(build.path("nextBuild"), "Next build") {
                        assertEquals(nextName, path("name").asText(), "Next build")
                    }
                } else {
                    assertJsonNull(build.path("nextBuild"), "No next build")
                }
            }
        }

        project {
            branch {
                (1..3).forEach { no ->
                    build("$no")
                }
                doTest(this, "1", null, "2")
                doTest(this, "2", "1", "3")
                doTest(this, "3", "2", null)
            }
        }
    }

    @Test
    fun `Finding validations from a list of names`() {
        project {
            branch {
                val vs1 = validationStamp()
                val vs2 = validationStamp()
                val vs3 = validationStamp()

                val build = build {
                    validate(vs1)
                    validate(vs2)
                    validate(vs3)
                }

                run(
                    """
                        query ValidationStampsByName(${'$'}validationStamps: [String!]!) {
                            build(id: ${build.id}) {
                                validations(validationStamps: ${'$'}validationStamps) {
                                    validationStamp {
                                        id
                                    }
                                }
                            }
                        }
                    """.trimIndent(),
                    mapOf("validationStamps" to listOf(vs1.name, vs3.name))
                ) { data ->
                    val vsIds = data.path("build").path("validations").map { run ->
                        run.path("validationStamp").path("id").asInt()
                    }
                    assertEquals(
                        listOf(vs1, vs3).sortedBy { it.name }.map { it.id() },
                        vsIds
                    )
                }
            }
        }
    }

}