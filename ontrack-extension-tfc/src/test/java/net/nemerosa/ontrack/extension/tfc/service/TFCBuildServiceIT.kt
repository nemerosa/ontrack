package net.nemerosa.ontrack.extension.tfc.service

import net.nemerosa.ontrack.extension.general.BuildLinkDisplayProperty
import net.nemerosa.ontrack.extension.general.BuildLinkDisplayPropertyType
import net.nemerosa.ontrack.extension.general.ReleaseProperty
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TFCBuildServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var tfcBuildService: TFCBuildService

    @Test
    fun `Build with branch and name`() {
        asAdmin {
            project {
                branch {
                    build {
                        val build = tfcBuildService.findBuild(
                                TFCParameters(
                                        project = project.name,
                                        branch = branch.name,
                                        build = name,
                                        validation = "vs",
                                )
                        )
                        assertNotNull(build, "Build found") {
                            assertEquals(this.id, it.id, "Build OK")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Build with Git branch and name`() {
        asAdmin {
            project {
                branch("release-1.1") {
                    build {
                        val build = tfcBuildService.findBuild(
                                TFCParameters(
                                        project = project.name,
                                        branch = "release/1.1",
                                        build = name,
                                        validation = "vs",
                                )
                        )
                        assertNotNull(build, "Build found") {
                            assertEquals(this.id, it.id, "Build OK")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Build with branch and label name`() {
        asAdmin {
            project {
                useLabel()
                branch {
                    build {
                        release("1.1.0")
                        val build = tfcBuildService.findBuild(
                                TFCParameters(
                                        project = project.name,
                                        branch = branch.name,
                                        build = "1.1.0",
                                        validation = "vs",
                                )
                        )
                        assertNotNull(build, "Build found") {
                            assertEquals(this.id, it.id, "Build OK")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Build with Git branch and label name`() {
        asAdmin {
            project {
                useLabel()
                branch("release-1.1") {
                    build {
                        release("1.1.0")
                        val build = tfcBuildService.findBuild(
                                TFCParameters(
                                        project = project.name,
                                        branch = "release/1.1",
                                        build = "1.1.0",
                                        validation = "vs",
                                )
                        )
                        assertNotNull(build, "Build found") {
                            assertEquals(this.id, it.id, "Build OK")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Build with project and name`() {
        asAdmin {
            project {
                branch {
                    build {
                        val build = tfcBuildService.findBuild(
                                TFCParameters(
                                        project = project.name,
                                        branch = null,
                                        build = name,
                                        validation = "vs",
                                )
                        )
                        assertNotNull(build, "Build found") {
                            assertEquals(this.id, it.id, "Build OK")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Build with project and label name`() {
        asAdmin {
            project {
                useLabel()
                branch {
                    build {
                        release("1.1.0")
                        val build = tfcBuildService.findBuild(
                                TFCParameters(
                                        project = project.name,
                                        branch = null,
                                        build = "1.1.0",
                                        validation = "vs",
                                )
                        )
                        assertNotNull(build, "Build found") {
                            assertEquals(this.id, it.id, "Build OK")
                        }
                    }
                }
            }
        }
    }

    private fun Project.useLabel() {
        setProperty(this, BuildLinkDisplayPropertyType::class.java, BuildLinkDisplayProperty(useLabel = true))
    }

    private fun Build.release(label: String) {
        setProperty(this, ReleasePropertyType::class.java, ReleaseProperty(name = label))
    }

}