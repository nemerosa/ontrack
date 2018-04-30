package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.structure.RunInfoInput
import net.nemerosa.ontrack.model.structure.RunInfoService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RunInfoServiceIT : AbstractServiceTestSupport() {

    @Autowired
    private lateinit var runInfoService: RunInfoService

    @Test
    fun `No run info by default for a build`() {
        val build = doCreateBuild()
        val info = runInfoService.getRunInfo(build)
        assertTrue(info.empty, "No run info")
    }

    @Test
    fun `Sets and gets the run info for a build`() {
        val build = doCreateBuild()
        val info = runInfoService.setRunInfo(
                build,
                RunInfoInput(
                        sourceType = "jenkins",
                        sourceUri = "http://jenkins/job/build/1",
                        triggerType = "scm",
                        triggerData = "1234cde",
                        runTime = 26
                )
        )
        assertFalse(info.empty)
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
        val info = runInfoService.setRunInfo(
                build,
                RunInfoInput(
                        sourceType = "jenkins",
                        sourceUri = "http://jenkins/job/build/1",
                        triggerType = "scm",
                        triggerData = "1234cde",
                        runTime = 26
                )
        )
        assertFalse(info.empty)
        // Deletion
        runInfoService.deleteRunInfo(build)
        val newInfo = runInfoService.getRunInfo(build)
        assertTrue(newInfo.empty, "No run info")
    }

}