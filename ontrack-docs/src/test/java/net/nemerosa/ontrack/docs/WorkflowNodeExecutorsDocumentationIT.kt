package net.nemerosa.ontrack.docs

import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import net.nemerosa.ontrack.model.annotations.getAPITypeDescription
import net.nemerosa.ontrack.model.docs.getDocumentationExampleCode
import net.nemerosa.ontrack.model.docs.getFieldsDocumentation
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.full.findAnnotations
import kotlin.test.fail

class WorkflowNodeExecutorsDocumentationIT : AbstractDocGenIT() {

    @Autowired
    private lateinit var workflowNodeExecutors: List<WorkflowNodeExecutor>

    @Test
    fun `Workflow node executors`() {

        fun getWNXFileId(wnx: WorkflowNodeExecutor): String =
            "workflow-node-executor-${wnx.id}"

        fun getWNXTitle(wnx: WorkflowNodeExecutor): String =
            "${wnx.displayName} (${wnx.id})"

        fun generateWNX(directoryContext: DocGenDirectoryContext, wnx: WorkflowNodeExecutor) {
            val description = getAPITypeDescription(wnx::class)
            val parameters = getFieldsDocumentation(wnx::class)
            val example = getDocumentationExampleCode(wnx::class)

            val fileId = getWNXFileId(wnx)

            val outputFieldsDocumentation = try {
                getFieldsDocumentation(wnx::class, section = "output", required = false)
            } catch (any: Exception) {
                fail("Failed to get output fields documentation for ${wnx::class.simpleName}", any)
            }

            directoryContext.writeFile(
                fileId = fileId,
                level = 4,
                title = getWNXTitle(wnx),
                header = description,
                fields = parameters,
                example = example,
                links = wnx::class.findAnnotations(),
                extendedConfig = { s ->
                    val output = outputFieldsDocumentation
                    if (output.isNotEmpty()) {
                        s.append("Output:\n\n")
                        directoryContext.writeFields(s, output)
                    }
                },
            )
        }

        docGenSupport.inDirectory("workflow-node-executors") {

            writeIndex(
                fileId = "appendix-workflow-node-executors-index",
                level = 4,
                title = "List of workflow node executors",
                items = workflowNodeExecutors.associate { workflowNodeExecutor ->
                    getWNXFileId(workflowNodeExecutor) to getWNXTitle(workflowNodeExecutor)
                }
            )

            workflowNodeExecutors.forEach { wnx ->
                generateWNX(this, wnx)
            }

        }
    }

}