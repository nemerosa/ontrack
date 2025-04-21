package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.BuildCreate
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.ValidationRunCreate
import net.nemerosa.ontrack.model.structure.RunInfo
import net.nemerosa.ontrack.model.structure.RunInfoInput
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.jupiter.api.Test
import org.springframework.security.access.AccessDeniedException
import kotlin.test.*

@AsAdminTest
class RunInfoServiceIT : AbstractDSLTestSupport() {

    @Test
    fun `Needs authorization to add run info to a build`() {
        val build = doCreateBuild()
        assertFailsWith(AccessDeniedException::class) {
            asUserWithView(build).execute {
                runInfoService.setRunInfo(build, RunInfoInput(runTime = 30))
            }
        }
        val info: RunInfo = asUser().withProjectFunction(build, BuildCreate::class.java).call {
            runInfoService.setRunInfo(build, RunInfoInput(runTime = 30))
        }
        assertEquals(30, info.runTime)
        // Deletion
        assertFailsWith(AccessDeniedException::class) {
            asUserWithView(build).execute {
                runInfoService.deleteRunInfo(build)
            }
        }
        val ack: Ack = asUser().withProjectFunction(build, ProjectEdit::class.java).call {
            runInfoService.deleteRunInfo(build)
        }
        assertTrue(ack.success)
    }

    @Test
    fun `Needs authorization to add run info to a validation run`() {
        val vs = doCreateValidationStamp()
        val build = doCreateBuild(vs.branch, nameDescription())
        val run = doValidateBuild(build, vs, ValidationRunStatusID.STATUS_PASSED)
        assertFailsWith(AccessDeniedException::class) {
            asUserWithView(run).execute {
                runInfoService.setRunInfo(run, RunInfoInput(runTime = 30))
            }
        }
        val info: RunInfo = asUser().withProjectFunction(run, ValidationRunCreate::class.java).call {
            runInfoService.setRunInfo(run, RunInfoInput(runTime = 30))
        }
        assertEquals(30, info.runTime)
    }

    @Test
    fun `No run info by default for a build`() {
        val build = doCreateBuild()
        val info = asUserWithView(build).call { runInfoService.getRunInfo(build) }
        assertNull(info, "No run info")
    }

    @Test
    fun `Sets and gets the run info for a build`() {
        val build = doCreateBuild()
        val info = asUser().withProjectFunction(build, BuildCreate::class.java).call {
            runInfoService.setRunInfo(
                build,
                RunInfoInput(
                    sourceType = "jenkins",
                    sourceUri = "http://jenkins/job/build/1",
                    triggerType = "scm",
                    triggerData = "1234cde",
                    runTime = 26
                )
            )
        }
        assertTrue(info.id != 0)
        assertEquals("jenkins", info.sourceType)
        assertEquals("http://jenkins/job/build/1", info.sourceUri)
        assertEquals("scm", info.triggerType)
        assertEquals("1234cde", info.triggerData)
        assertEquals(26, info.runTime)
    }

    @Test
    fun `Sets and deletes the run info for a build`() {
        val build = doCreateBuild()
        asUser().withProjectFunction(build, BuildCreate::class.java).call {
            runInfoService.setRunInfo(
                build,
                RunInfoInput(
                    sourceType = "jenkins",
                    sourceUri = "http://jenkins/job/build/1",
                    triggerType = "scm",
                    triggerData = "1234cde",
                    runTime = 26
                )
            )
        }
        // Deletion
        asUser().withProjectFunction(build, ProjectEdit::class.java).execute {
            runInfoService.deleteRunInfo(build)
        }
        val newInfo = asUserWithView(build).call { runInfoService.getRunInfo(build) }
        assertNull(newInfo, "No run info")
    }

    @Test
    fun `Sets and deletes the run info for a validation run`() {
        project {
            branch {
                val vs = validationStamp()
                build {
                    val run = validate(vs)
                    runInfoService.setRunInfo(
                        run,
                        RunInfoInput(
                            sourceType = "jenkins",
                            sourceUri = "http://jenkins/job/build/1",
                            triggerType = "scm",
                            triggerData = "1234cde",
                            runTime = 26
                        )
                    )
                    assertNotNull(runInfoService.getRunInfo(run), "Validatio run run info set") {
                        assertEquals("jenkins", it.sourceType)
                        assertEquals("http://jenkins/job/build/1", it.sourceUri)
                        assertEquals("scm", it.triggerType)
                        assertEquals("1234cde", it.triggerData)
                        assertEquals(26, it.runTime)
                    }
                }
            }
        }
    }

}