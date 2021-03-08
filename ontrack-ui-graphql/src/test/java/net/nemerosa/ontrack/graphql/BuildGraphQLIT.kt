package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.json.isNullOrNullNode
import net.nemerosa.ontrack.model.structure.Build
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration tests around the `builds` root query.
 */
class BuildGraphQLIT : AbstractQLKTITSupport() {

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
                        builds(project: "${project.name}", branch: "${name}", name: "11") {
                            id
                        }
                    }""")
                }
                val list = data["builds"]
                assertEquals(0, list.size())
            }
        }
    }

}