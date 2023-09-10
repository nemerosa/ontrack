package net.nemerosa.ontrack.extension.av.validation

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.general.autoValidationStampProperty
import net.nemerosa.ontrack.extension.scm.service.TestSCMExtension
import net.nemerosa.ontrack.model.structure.Build
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
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

                run("""
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
                """) { data ->
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

}