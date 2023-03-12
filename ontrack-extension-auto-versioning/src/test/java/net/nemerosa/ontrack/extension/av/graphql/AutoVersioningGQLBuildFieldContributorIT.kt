package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfig
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.test.assertJsonNotNull
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AutoVersioningGQLBuildFieldContributorIT : AbstractAutoVersioningTestSupport() {

    @Test
    fun `Get AV status between two builds`() {
        asAdmin {
            val dependencyBuild = project<Build> {
                branch<Build>("release/1.1") {
                    build("1")
                }
            }
            val parentBuild = project<Build> {
                branch<Build> {
                    autoVersioningConfigurationService.setupAutoVersioning(
                        this,
                        AutoVersioningConfig(
                            configurations = listOf(
                                AutoVersioningTestFixtures.sourceConfig(
                                    sourceProject = dependencyBuild.project.name,
                                    sourceBranch = "release/1.1"
                                )
                            )
                        )
                    )
                    build("parent-1")
                }
            }
            run(
                """
                {
                    build(id: ${dependencyBuild.id}) {
                        autoVersioning(buildId: ${parentBuild.id}) {
                            config {
                                sourceProject
                                sourceBranch
                            }
                        }
                    }
                }
            """
            ) { data ->
                assertJsonNotNull(
                    data.path("build").path("autoVersioning"),
                    "AV config found"
                ) {
                    assertEquals(dependencyBuild.project.name, path("config").path("sourceProject").asText())
                    assertEquals("release/1.1", path("config").path("sourceBranch").asText())
                }
            }
        }
    }

    @Test
    fun `Get AV status between one build and a branch`() {
        asAdmin {
            val dependencyBuild = project<Build> {
                branch<Build>("release/1.1") {
                    build("1")
                }
            }
            val parentBuild = project<Build> {
                branch<Build> {
                    autoVersioningConfigurationService.setupAutoVersioning(
                        this,
                        AutoVersioningConfig(
                            configurations = listOf(
                                AutoVersioningTestFixtures.sourceConfig(
                                    sourceProject = dependencyBuild.project.name,
                                    sourceBranch = "release/1.1"
                                )
                            )
                        )
                    )
                    build("parent-1")
                }
            }
            run(
                """
                {
                    build(id: ${dependencyBuild.id}) {
                        autoVersioning(branchId: ${parentBuild.branch.id}) {
                            config {
                                sourceProject
                                sourceBranch
                            }
                        }
                    }
                }
            """
            ) { data ->
                assertJsonNotNull(
                    data.path("build").path("autoVersioning"),
                    "AV config found"
                ) {
                    assertEquals(dependencyBuild.project.name, path("config").path("sourceProject").asText())
                    assertEquals("release/1.1", path("config").path("sourceBranch").asText())
                }
            }
        }
    }

    @Test
    fun `Get AV status between two builds using the UP direction`() {
        asAdmin {
            val dependencyBuild = project<Build> {
                branch<Build>("release/1.1") {
                    build("1")
                }
            }
            val parentBuild = project<Build> {
                branch<Build> {
                    autoVersioningConfigurationService.setupAutoVersioning(
                        this,
                        AutoVersioningConfig(
                            configurations = listOf(
                                AutoVersioningTestFixtures.sourceConfig(
                                    sourceProject = dependencyBuild.project.name,
                                    sourceBranch = "release/1.1"
                                )
                            )
                        )
                    )
                    build("parent-1")
                }
            }
            run(
                """
                {
                    build(id: ${parentBuild.id}) {
                        autoVersioning(buildId: ${dependencyBuild.id}, direction: UP) {
                            config {
                                sourceProject
                                sourceBranch
                            }
                        }
                    }
                }
            """
            ) { data ->
                assertJsonNotNull(
                    data.path("build").path("autoVersioning"),
                    "AV config found"
                ) {
                    assertEquals(dependencyBuild.project.name, path("config").path("sourceProject").asText())
                    assertEquals("release/1.1", path("config").path("sourceBranch").asText())
                }
            }
        }
    }

    @Test
    fun `Get AV status between one build and a branch using the UP direction`() {
        asAdmin {
            val dependencyBuild = project<Build> {
                branch<Build>("release/1.1") {
                    build("1")
                }
            }
            val parentBuild = project<Build> {
                branch<Build> {
                    autoVersioningConfigurationService.setupAutoVersioning(
                        this,
                        AutoVersioningConfig(
                            configurations = listOf(
                                AutoVersioningTestFixtures.sourceConfig(
                                    sourceProject = dependencyBuild.project.name,
                                    sourceBranch = "release/1.1"
                                )
                            )
                        )
                    )
                    build("parent-1")
                }
            }
            run(
                """
                {
                    build(id: ${parentBuild.id}) {
                        autoVersioning(branchId: ${dependencyBuild.branch.id}, direction: UP) {
                            config {
                                sourceProject
                                sourceBranch
                            }
                        }
                    }
                }
            """
            ) { data ->
                assertJsonNotNull(
                    data.path("build").path("autoVersioning"),
                    "AV config found"
                ) {
                    assertEquals(dependencyBuild.project.name, path("config").path("sourceProject").asText())
                    assertEquals("release/1.1", path("config").path("sourceBranch").asText())
                }
            }
        }
    }

}