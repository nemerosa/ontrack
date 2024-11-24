package net.nemerosa.ontrack.kdsl.acceptance.tests.environments.workflows

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.SlotPipelineStatus
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.SlotWorkflowTrigger
import net.nemerosa.ontrack.kdsl.spec.extension.environments.environments
import net.nemerosa.ontrack.kdsl.spec.extension.environments.workflows.addWorkflow
import org.junit.jupiter.api.Test

class ACCEnvironmentsWorkflows : AbstractACCDSLTestSupport() {

    @Test
    fun `Creating workflow triggering the start of the deployment`() {
        // Creating an application project
        val application = project { this }
        val applicationBranch = application.branch { this }

        // Creating an environment
        val environment = ontrack.environments.createEnvironment(
            name = uid("env-"),
            order = 0,
        )
        // Creating a slot for the environment & the project
        val slot = environment.createSlot(
            project = application,
        )

        // Adding a workflow to this slot
        slot.addWorkflow(
            trigger = SlotWorkflowTrigger.DEPLOYING,
            workflowYaml = """
                name: Creation
                nodes:
                  - id: deploying
                    executorId: slot-pipeline-deploying
                    data: {}
            """.trimIndent()
        )

        // Creating a build for the application project
        val build = applicationBranch.build(name = "1.0.0") { this }
        // Creating a pipeline for this build & starting its deployment
        val pipeline = slot.createPipeline(build = build)

        // We expect the pipeline to have started its deployment
        waitUntil(
            task = "Pipeline deploying",
            timeout = 10_000,
            interval = 1_000,
        ) {
            ontrack.environments.findPipelineById(pipeline.id)?.status == SlotPipelineStatus.DEPLOYING
        }

    }

    @Test
    fun `Deployment workflow triggering the end of the deployment`() {
        // Creating an application project
        val application = project { this }
        val applicationBranch = application.branch { this }

        // Creating an environment
        val environment = ontrack.environments.createEnvironment(
            name = uid("env-"),
            order = 0,
        )
        // Creating a slot for the environment & the project
        val slot = environment.createSlot(
            project = application,
        )

        // Adding a workflow to this slot
        slot.addWorkflow(
            trigger = SlotWorkflowTrigger.DEPLOYING,
            workflowYaml = """
                name: Deployment
                nodes:
                  - id: deployed
                    executorId: slot-pipeline-deployed
                    data: {}
            """.trimIndent()
        )

        // Creating a build for the application project
        val build = applicationBranch.build(name = "1.0.0") { this }
        // Creating a pipeline for this build & starting its deployment
        val pipeline = slot.createPipeline(build = build)
        pipeline.startDeploying()

        // We expect the pipeline to become deployed
        waitUntil(
            task = "Pipeline deployed",
            timeout = 10_000,
            interval = 1_000,
        ) {
            ontrack.environments.findPipelineById(pipeline.id)?.status == SlotPipelineStatus.DEPLOYED
        }

    }

}