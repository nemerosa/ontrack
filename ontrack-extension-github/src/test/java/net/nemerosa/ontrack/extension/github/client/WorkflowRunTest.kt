package net.nemerosa.ontrack.extension.github.client

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WorkflowRunTest {

    @Test
    fun `Workflow not finished`() {
        val run = workflowRun(
            status = "in_progress",
            conclusion = null,
        )
        assertEquals(null, run.success)
    }

    @Test
    fun `Workflow finished and successful`() {
        val run = workflowRun(
            status = "completed",
            conclusion = "success",
        )
        assertEquals(true, run.success)
    }

    @Test
    fun `Workflow finished and not successful`() {
        val run = workflowRun(
            status = "completed",
            conclusion = "error",
        )
        assertEquals(false, run.success)
    }

    private fun workflowRun(
        status: String,
        conclusion: String?,
    ) = WorkflowRun(
        id = 1L,
        headBranch = "main",
        status = status,
        conclusion = conclusion,
    )

}