package net.nemerosa.ontrack.extension.github.workflow

import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunTest.Companion.run
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class BuildGitHubWorkflowRunPropertyTest {


    @Test
    fun `Finding a run`() {
        val run1 = run(1L, running = true)
        val run2 = run(2L, running = true)
        val property = BuildGitHubWorkflowRunProperty(
            workflows = listOf(
                run1,
                run2,
            )
        )
        assertEquals(run1, property.findRun(1L))
        assertEquals(run2, property.findRun(2L))
        assertEquals(null, property.findRun(3L))
    }
}