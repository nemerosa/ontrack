package net.nemerosa.ontrack.extension.github.workflow

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class BuildGitHubWorkflowRunTest {

    @Test
    fun `Replacing a run by another`() {
        val list = mutableListOf(
            run(1L, running = true),
            run(2L, running = true),
        )
        val edited = BuildGitHubWorkflowRun.edit(list, run(1L, running = false))
        assertTrue(edited, "Replacement occurred")
        assertEquals(
            mutableListOf(
                run(1L, running = false),
                run(2L, running = true),
            ),
            list
        )
    }

    @Test
    fun `Not replacing a run by another`() {
        val list = mutableListOf(
            run(1L, running = true),
            run(2L, running = true),
        )
        val edited = BuildGitHubWorkflowRun.edit(list, run(1L, running = true))
        assertFalse(edited, "Replacement did not occur")
        assertEquals(
            mutableListOf(
                run(1L, running = true),
                run(2L, running = true),
            ),
            list
        )
    }

    @Test
    fun `Adding a new element`() {
        val list = mutableListOf(
            run(1L, running = true),
            run(2L, running = true),
        )
        val edited = BuildGitHubWorkflowRun.edit(list, run(3L, running = true))
        assertTrue(edited, "List changed")
        assertEquals(
            mutableListOf(
                run(1L, running = true),
                run(2L, running = true),
                run(3L, running = true),
            ),
            list
        )
    }

    companion object {
        fun run(
            runId: Long,
            running: Boolean = true,
        ) = BuildGitHubWorkflowRun(
            runId = runId,
            url = "run//$runId",
            name = runId.toString(),
            runNumber = 1,
            running = running,
            event = "push",
        )
    }

}