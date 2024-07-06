package net.nemerosa.ontrack.extension.workflows.definition

import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.TextNode

object WorkflowFixtures {

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