package net.nemerosa.ontrack.extension.av.validation

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.general.autoValidationStampProperty
import net.nemerosa.ontrack.extension.general.metaInfoItem
import net.nemerosa.ontrack.extension.general.metaInfoProperty
import net.nemerosa.ontrack.extension.scm.service.TestSCMExtension
import net.nemerosa.ontrack.model.structure.Build
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import kotlin.jvm.optionals.getOrNull
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class AutoVersioningValidationServiceIT : AbstractAutoVersioningTestSupport() {

    @Autowired
    private lateinit var autoVersioningValidationService: AutoVersioningValidationService

    @Autowired
    private lateinit var testSCMExtension: TestSCMExtension

    @Test
    fun `Check and validate for no configuration at all`() {
        project {
            branch {
                val build = build()
                val data = autoVersioningValidationService.checkAndValidate(build)
                assertTrue(data.isEmpty(), "No data returned")
            }
        }
    }

    @Test
    fun `Check and validate for no configuration`() {
        project {
            branch {
                setAutoVersioning {
                    // No configuration
                }
                val build = build()
                val data = autoVersioningValidationService.checkAndValidate(build)
                assertTrue(data.isEmpty(), "No data returned")
            }
        }
    }

    @Test
    fun `Check and validate for no validation stamp`() {
        project {
            branch {
                setAutoVersioning {
                    autoVersioningConfig {
                        project = "source"
                        branch = "main"
                        promotion = "GOLD"
                        validationStamp = null
                    }
                }
                val build = build()
                val data = autoVersioningValidationService.checkAndValidate(build)
                assertTrue(data.isEmpty(), "No data returned")
            }
        }
    }

    @Test
    fun `Check and validate for outdated dependency read from the linked build with auto validation`() {
        val (linkedBuild430, _) = project<Pair<Build, Build>> {
            branch<Pair<Build, Build>>("main") {
                val gold = promotionLevel("GOLD")
                val b430 = build("4.3.0")
                b430.promote(gold)
                val b431 = build("4.3.1")
                b431.promote(gold)
                b430 to b431
            }
        }

        project {
            branch {
                autoValidationStampProperty(project, autoCreateIfNotPredefined = true)
                setAutoVersioning {
                    autoVersioningConfig {
                        project = linkedBuild430.project.name
                        branch = linkedBuild430.branch.name
                        promotion = "GOLD"
                        validationStamp = "auto"
                    }
                }
                val build = build()
                build.linkTo(linkedBuild430)

                val data = autoVersioningValidationService.checkAndValidate(build)

                assertNotNull(data.firstOrNull(), "One validation") { validationData ->
                    assertEquals(linkedBuild430.project.name, validationData.project)
                    assertEquals("4.3.0", validationData.version)
                    assertEquals("4.3.1", validationData.latestVersion)
                    assertEquals("gradle.properties", validationData.path)
                    assertTrue(validationData.time > 0)
                }

                val vs = structureService.findValidationStampByName(
                    build.project.name, build.branch.name, "auto-versioning-${linkedBuild430.project.name}"
                ).getOrNull() ?: fail("Getting the auto versioning validation stamp")

                val run =
                    structureService.getValidationRunsForBuildAndValidationStamp(build.id, vs.id, 0, 1).firstOrNull()
                assertNotNull(run, "Validation has been created") {
                    assertEquals("FAILED", it.lastStatusId, "Validation failed")
                }
            }
        }
    }

    @Test
    fun `Check and validate for outdated dependency read from the linked build with existing validation stamp`() {
        val (linkedBuild430, _) = project<Pair<Build, Build>> {
            branch<Pair<Build, Build>>("main") {
                val gold = promotionLevel("GOLD")
                val b430 = build("4.3.0")
                b430.promote(gold)
                val b431 = build("4.3.1")
                b431.promote(gold)
                b430 to b431
            }
        }

        project {
            branch {
                val vs = validationStamp()
                autoValidationStampProperty(project, autoCreateIfNotPredefined = true)
                setAutoVersioning {
                    autoVersioningConfig {
                        project = linkedBuild430.project.name
                        branch = linkedBuild430.branch.name
                        promotion = "GOLD"
                        validationStamp = vs.name
                    }
                }
                val build = build()
                build.linkTo(linkedBuild430)

                run(
                    """
                    mutation {
                        checkAutoVersioning(input: {
                            project: "${build.project.name}",
                            branch: "${build.branch.name}",
                            build: "${build.name}",
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """
                ) { data ->
                    checkGraphQLUserErrors(data, "checkAutoVersioning")
                }

                val run =
                    structureService.getValidationRunsForBuildAndValidationStamp(build.id, vs.id, 0, 1).firstOrNull()
                assertNotNull(run, "Validation has been created") {
                    assertEquals("FAILED", it.lastStatusId, "Validation failed")
                }
            }
        }
    }

    @Test
    fun `Check and validate for up-to-date dependency read from the linked build with existing validation stamp`() {
        val (linkedBuild430, linkedBuild431) = project<Pair<Build, Build>> {
            branch<Pair<Build, Build>>("main") {
                val gold = promotionLevel("GOLD")
                val b430 = build("4.3.0")
                b430.promote(gold)
                val b431 = build("4.3.1")
                b431.promote(gold)
                b430 to b431
            }
        }

        project {
            branch {
                val vs = validationStamp()
                autoValidationStampProperty(project, autoCreateIfNotPredefined = true)
                setAutoVersioning {
                    autoVersioningConfig {
                        project = linkedBuild430.project.name
                        branch = linkedBuild430.branch.name
                        promotion = "GOLD"
                        validationStamp = vs.name
                    }
                }
                val build = build()
                build.linkTo(linkedBuild431)

                val data = autoVersioningValidationService.checkAndValidate(build)

                assertNotNull(data.firstOrNull(), "One validation") { validationData ->
                    assertEquals(linkedBuild430.project.name, validationData.project)
                    assertEquals("4.3.1", validationData.version)
                    assertEquals("4.3.1", validationData.latestVersion)
                    assertEquals("gradle.properties", validationData.path)
                    assertTrue(validationData.time > 0)
                }

                val run =
                    structureService.getValidationRunsForBuildAndValidationStamp(build.id, vs.id, 0, 1).firstOrNull()
                assertNotNull(run, "Validation has been created") {
                    assertEquals("PASSED", it.lastStatusId, "Validation passed")
                }
            }
        }
    }

    @Test
    fun `Check and validate using GraphQL for up-to-date dependency read from the linked build with existing validation stamp`() {
        val (linkedBuild430, linkedBuild431) = project<Pair<Build, Build>> {
            branch<Pair<Build, Build>>("main") {
                val gold = promotionLevel("GOLD")
                val b430 = build("4.3.0")
                b430.promote(gold)
                val b431 = build("4.3.1")
                b431.promote(gold)
                b430 to b431
            }
        }

        project {
            branch {
                val vs = validationStamp()
                autoValidationStampProperty(project, autoCreateIfNotPredefined = true)
                setAutoVersioning {
                    autoVersioningConfig {
                        project = linkedBuild430.project.name
                        branch = linkedBuild430.branch.name
                        promotion = "GOLD"
                        validationStamp = vs.name
                    }
                }
                val build = build()
                build.linkTo(linkedBuild431)

                val data = autoVersioningValidationService.checkAndValidate(build)

                assertNotNull(data.firstOrNull(), "One validation") { validationData ->
                    assertEquals(linkedBuild430.project.name, validationData.project)
                    assertEquals("4.3.1", validationData.version)
                    assertEquals("4.3.1", validationData.latestVersion)
                    assertEquals("gradle.properties", validationData.path)
                    assertTrue(validationData.time > 0)
                }

                val run =
                    structureService.getValidationRunsForBuildAndValidationStamp(build.id, vs.id, 0, 1).firstOrNull()
                assertNotNull(run, "Validation has been created") {
                    assertEquals("PASSED", it.lastStatusId, "Validation passed")
                }
            }
        }
    }

    @Test
    fun `Check and validate using GraphQL for up-to-date dependency read from the linked build with non existing validation stamp`() {
        val (linkedBuild430, linkedBuild431) = project<Pair<Build, Build>> {
            branch<Pair<Build, Build>>("main") {
                val gold = promotionLevel("GOLD")
                val b430 = build("4.3.0")
                b430.promote(gold)
                val b431 = build("4.3.1")
                b431.promote(gold)
                b430 to b431
            }
        }

        project {
            branch {
                val vs = validationStamp()
                // autoValidationStampProperty(project, autoCreateIfNotPredefined = true)
                setAutoVersioning {
                    autoVersioningConfig {
                        project = linkedBuild430.project.name
                        branch = linkedBuild430.branch.name
                        promotion = "GOLD"
                        validationStamp = vs.name
                    }
                }
                val build = build()
                build.linkTo(linkedBuild431)

                val data = autoVersioningValidationService.checkAndValidate(build)

                assertNotNull(data.firstOrNull(), "One validation") { validationData ->
                    assertEquals(linkedBuild430.project.name, validationData.project)
                    assertEquals("4.3.1", validationData.version)
                    assertEquals("4.3.1", validationData.latestVersion)
                    assertEquals("gradle.properties", validationData.path)
                    assertTrue(validationData.time > 0)
                }

                val run =
                    structureService.getValidationRunsForBuildAndValidationStamp(build.id, vs.id, 0, 1).firstOrNull()
                assertNotNull(run, "Validation has been created") {
                    assertEquals("PASSED", it.lastStatusId, "Validation passed")
                }
            }
        }
    }

    @Test
    fun `Check and validate for up-to-date dependency read from the SCM with existing validation stamp`() {
        val (linkedBuild430, linkedBuild431) = project<Pair<Build, Build>> {
            branch<Pair<Build, Build>>("main") {
                val gold = promotionLevel("GOLD")
                val b430 = build("4.3.0")
                b430.promote(gold)
                val b431 = build("4.3.1")
                b431.promote(gold)
                b430 to b431
            }
        }

        project {
            branch {
                testSCMExtension.registerProjectForTestSCM(project) {
                    withFile("gradle.properties", branch = name) {
                        "version = 4.3.1"
                    }
                }
                val vs = validationStamp()
                autoValidationStampProperty(project, autoCreateIfNotPredefined = true)
                setAutoVersioning {
                    autoVersioningConfig {
                        project = linkedBuild430.project.name
                        branch = linkedBuild430.branch.name
                        promotion = "GOLD"
                        validationStamp = vs.name
                    }
                }
                val build = build()

                val data = autoVersioningValidationService.checkAndValidate(build)

                assertNotNull(data.firstOrNull(), "One validation") { validationData ->
                    assertEquals(linkedBuild430.project.name, validationData.project)
                    assertEquals("4.3.1", validationData.version)
                    assertEquals("4.3.1", validationData.latestVersion)
                    assertEquals("gradle.properties", validationData.path)
                    assertTrue(validationData.time > 0)
                }

                val run =
                    structureService.getValidationRunsForBuildAndValidationStamp(build.id, vs.id, 0, 1).firstOrNull()
                assertNotNull(run, "Validation has been created") {
                    assertEquals("PASSED", it.lastStatusId, "Validation passed")
                }

                // Checks that the build link has been created
                structureService.isLinkedTo(build, linkedBuild431.project.name, linkedBuild431.name)
            }
        }
    }

    @Test
    fun `Check and validate for up-to-date dependency read from the SCM with existing validation stamp using the &same branch expression`() {
        val (linkedBuild430, linkedBuild431) = project<Pair<Build, Build>> {
            branch<Pair<Build, Build>>("main") {
                val gold = promotionLevel("GOLD")
                val b430 = build("4.3.0")
                b430.promote(gold)
                val b431 = build("4.3.1")
                b431.promote(gold)
                b430 to b431
            }
        }

        project {
            branch(name = linkedBuild430.branch.name) { // Making sure we have the same branch name to use "&same"
                testSCMExtension.registerProjectForTestSCM(project) {
                    withFile("gradle.properties", branch = name) {
                        "version = 4.3.1"
                    }
                }
                val vs = validationStamp()
                autoValidationStampProperty(project, autoCreateIfNotPredefined = true)
                setAutoVersioning {
                    autoVersioningConfig {
                        project = linkedBuild430.project.name
                        branch = "&same" // linkedBuild430.branch.name
                        promotion = "GOLD"
                        validationStamp = vs.name
                    }
                }
                val build = build()

                val data = autoVersioningValidationService.checkAndValidate(build)

                assertNotNull(data.firstOrNull(), "One validation") { validationData ->
                    assertEquals(linkedBuild430.project.name, validationData.project)
                    assertEquals("4.3.1", validationData.version)
                    assertEquals("4.3.1", validationData.latestVersion)
                    assertEquals("gradle.properties", validationData.path)
                    assertTrue(validationData.time > 0)
                }

                val run =
                    structureService.getValidationRunsForBuildAndValidationStamp(build.id, vs.id, 0, 1).firstOrNull()
                assertNotNull(run, "Validation has been created") {
                    assertEquals("PASSED", it.lastStatusId, "Validation passed")
                }

                // Checks that the build link has been created
                structureService.isLinkedTo(build, linkedBuild431.project.name, linkedBuild431.name)
            }
        }
    }

    @Test
    fun `Check and validate for up-to-date dependency read from the SCM with existing validation stamp and qualified link`() {
        val (linkedBuild430, linkedBuild431) = project<Pair<Build, Build>> {
            branch<Pair<Build, Build>>("main") {
                val gold = promotionLevel("GOLD")
                val b430 = build("4.3.0")
                b430.promote(gold)
                val b431 = build("4.3.1")
                b431.promote(gold)
                b430 to b431
            }
        }

        project {
            branch {
                testSCMExtension.registerProjectForTestSCM(project) {
                    withFile("gradle.properties", branch = name) {
                        "version = 4.3.1"
                    }
                }
                val vs = validationStamp()
                autoValidationStampProperty(project, autoCreateIfNotPredefined = true)
                setAutoVersioning {
                    autoVersioningConfig {
                        project = linkedBuild430.project.name
                        branch = linkedBuild430.branch.name
                        promotion = "GOLD"
                        validationStamp = vs.name
                        qualifier = "dep1"
                    }
                }
                val build = build()

                val data = autoVersioningValidationService.checkAndValidate(build)

                assertNotNull(data.firstOrNull(), "One validation") { validationData ->
                    assertEquals(linkedBuild430.project.name, validationData.project)
                    assertEquals("4.3.1", validationData.version)
                    assertEquals("4.3.1", validationData.latestVersion)
                    assertEquals("gradle.properties", validationData.path)
                    assertTrue(validationData.time > 0)
                }

                val run =
                    structureService.getValidationRunsForBuildAndValidationStamp(build.id, vs.id, 0, 1).firstOrNull()
                assertNotNull(run, "Validation has been created") {
                    assertEquals("PASSED", it.lastStatusId, "Validation passed")
                }

                // Checks that the build link has been created
                structureService.isLinkedTo(build, linkedBuild431.project.name, linkedBuild431.name, qualifier = "dep1")
            }
        }
    }

    @Test
    fun `Check and validate based on a floating version stored in meta information`() {
        project {
            branch {
                val dependency = this
                // Promotion
                val pl = promotionLevel()
                // Creates some builds whose "version" is floating (snapshot, yes, I know, very bad)
                // and stored into the meta information
                build {
                    metaInfoProperty(this, metaInfoItem("myVersion", "1-SNAPSHOT"))
                    promote(pl)
                }
                build {
                    metaInfoProperty(this, metaInfoItem("myVersion", "2-SNAPSHOT"))
                    promote(pl)
                }
                val lastDependencyBuild = build {
                    metaInfoProperty(this, metaInfoItem("myVersion", "2-SNAPSHOT"))
                    promote(pl)
                }

                // Now, let's focus on the target
                project {
                    branch {
                        val vs = validationStamp("av-my-version")
                        setAutoVersioning {
                            autoVersioningConfig {
                                project = dependency.project.name
                                branch = dependency.name
                                promotion = pl.name
                                targetProperty = "myVersion"
                                versionSource = "metaInfo/myVersion"
                                validationStamp = "av-my-version"
                            }
                        }
                        testSCMExtension.registerProjectForTestSCM(project) {
                            withFile("gradle.properties", branch = name) {
                                "myVersion = 2-SNAPSHOT" // Up to date
                            }
                        }

                        // Creating a build
                        val build = build()

                        // Running the check
                        autoVersioningValidationService.checkAndValidate(build)

                        // Gets the validation
                        val run =
                            structureService.getValidationRunsForBuildAndValidationStamp(build.id, vs.id, 0, 1)
                                .firstOrNull()
                        assertNotNull(run, "Validation has been created") {
                            assertEquals("PASSED", it.lastStatusId, "Validation passed")
                        }

                        // Checks that the build link has been created
                        assertTrue(
                            structureService.isLinkedTo(
                                build,
                                lastDependencyBuild.project.name,
                                lastDependencyBuild.name
                            ),
                            "Link to the last eligible build"
                        )
                    }
                }

            }
        }
    }

}