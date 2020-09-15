package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.model.structure.Build
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration tests around the `builds` root query.
 */
class BuildGraphQLIT : AbstractQLKTITSupport() {

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
                            uses {
                                name
                            }
                        }
                    }"""
                    ).run {
                        this["builds"][0]["uses"].map { it["name"].asText() }.toSet()
                    }.run {
                        assertEquals(
                                setOf("1.0", "2.0", "3.0"),
                                this
                        )
                    }

                    // Filter by project
                    run("""{
                        builds(id: $id) {
                            uses(project: "${ref1.name}") {
                                name
                            }
                        }
                    }"""
                    ).run {
                        this["builds"][0]["uses"].map { it["name"].asText() }.toSet()
                    }.run {
                        assertEquals(
                                setOf("1.0", "2.0"),
                                this
                        )
                    }

                    // Filter by branch
                    run("""{
                        builds(id: $id) {
                            uses(project: "${ref1.name}", branch: "master") {
                                name
                            }
                        }
                    }"""
                    ).run {
                        this["builds"][0]["uses"].map { it["name"].asText() }.toSet()
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
                uses {
                    name
                }
                usedBy {
                    pageItems {
                        name
                    }
                }
            }
        }""")

        val b = data["builds"].first()
        assertNotNull(b["uses"]) {
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
                uses {
                    name
                    branch {
                        name
                        project {
                            name
                        }
                    }
                }
            }
        }""")

        val links = data["builds"].first()["uses"]
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
                uses {
                    id
                    name
                    direction
                }
            }
        }""")
        // Checks the result
        val links = data["builds"].first()["uses"]
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
    fun `Following build links BOTH`() {
        // Three builds
        val a = doCreateBuild()
        val b = doCreateBuild()
        val c = doCreateBuild()
        // Links: a -> b -> c
        asAdmin().execute {
            structureService.addBuildLink(a, b)
            structureService.addBuildLink(b, c)
        }
        // Query build links of "b" BOTH
        val data = run("""{
            builds(id: ${b.id}) {
                linkedBuilds(direction: BOTH) {
                    id
                    name
                    direction
                }
            }
        }""")
        // Checks the result
        val links = data["builds"].first()["linkedBuilds"]
        assertNotNull(links) {
            assertEquals(2, it.size())
            val cLink = it[0]
            assertEquals(c.name, cLink["name"].asText())
            assertEquals(c.id(), cLink["id"].asInt())
            assertEquals("to", cLink.path("direction").asText())
            val aLink = it[1]
            assertEquals(a.name, aLink["name"].asText())
            assertEquals(a.id(), aLink["id"].asInt())
            assertEquals("from", aLink.path("direction").asText())
        }
    }

    @Test(expected = AssertionError::class)
    fun `Following build links with unknown direction`() {
        val b = doCreateBuild()
        run("""{
            builds(id: ${b.id}) {
                linkedBuilds(direction: TBD) {
                    id
                    name
                }
            }
        }""")
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

}