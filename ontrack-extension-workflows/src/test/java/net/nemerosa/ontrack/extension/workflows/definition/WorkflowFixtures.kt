package net.nemerosa.ontrack.extension.workflows.definition

import com.fasterxml.jackson.databind.node.TextNode

object WorkflowFixtures {

    val simpleLinearWorkflowYaml = """
        name: Simple Linear
        nodes:
          - id: start
            executorId: mock
            data:
              text: Start node
          - id: end
            parents:
              - id: start
            executorId: mock
            data:
              text: End node
    """.trimIndent()

    @Deprecated("Use YAML instead")
    fun simpleLinearWorkflow(
        name: String = "Simple linear"
    ) =
        Workflow(
            name = name,
            nodes = listOf(
                WorkflowNode(
                    id = "start",
                    executorId = "mock",
                    data = TextNode("Start node"),
                    parents = emptyList(),
                ),
                WorkflowNode(
                    id = "end",
                    executorId = "mock",
                    data = TextNode("End node"),
                    parents = listOf(
                        WorkflowParentNode(
                            id = "start"
                        )
                    ),
                ),
            )
        )

    @Deprecated("Use YAML instead")
    fun cyclicWorkflow() =
        Workflow(
            name = "Simple cyclic",
            nodes = listOf(
                WorkflowNode(
                    id = "start",
                    executorId = "mock",
                    data = TextNode("Start node"),
                    parents = listOf(
                        WorkflowParentNode(
                            id = "end",
                        )
                    ),
                ),
                WorkflowNode(
                    id = "end",
                    executorId = "mock",
                    data = TextNode("End node"),
                    parents = listOf(
                        WorkflowParentNode(
                            id = "start"
                        )
                    ),
                ),
            )
        )

    @Deprecated("Use YAML instead")
    fun twoParallelAndJoin() =
        Workflow(
            name = "Parallel with join",
            nodes = listOf(
                WorkflowNode(
                    id = "start-a",
                    executorId = "mock",
                    data = TextNode("Start node A"),
                    parents = emptyList(),
                ),
                WorkflowNode(
                    id = "start-b",
                    executorId = "mock",
                    data = TextNode("Start node B"),
                    parents = emptyList(),
                ),
                WorkflowNode(
                    id = "end",
                    executorId = "mock",
                    data = TextNode("End node"),
                    parents = listOf(
                        WorkflowParentNode(
                            id = "start-a",
                        ),
                        WorkflowParentNode(
                            id = "start-b",
                        ),
                    ),
                ),
            )
        )

}