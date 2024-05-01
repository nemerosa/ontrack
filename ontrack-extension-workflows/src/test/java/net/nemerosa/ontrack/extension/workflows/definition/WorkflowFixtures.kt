package net.nemerosa.ontrack.extension.workflows.definition

import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.TextNode

object WorkflowFixtures {

    fun simpleLinearWorkflow() =
        Workflow(
            name = "Simple linear",
            data = NullNode.instance,
            nodes = listOf(
                WorkflowNode(
                    id = "start",
                    data = TextNode("Start node"),
                    parents = emptyList(),
                ),
                WorkflowNode(
                    id = "end",
                    data = TextNode("End node"),
                    parents = listOf("start"),
                ),
            )
        )

    fun twoParallelAndJoin() =
        Workflow(
            name = "Parallel with join",
            data = NullNode.instance,
            nodes = listOf(
                WorkflowNode(
                    id = "start-a",
                    data = TextNode("Start node A"),
                    parents = emptyList(),
                ),
                WorkflowNode(
                    id = "start-b",
                    data = TextNode("Start node B"),
                    parents = emptyList(),
                ),
                WorkflowNode(
                    id = "end",
                    data = TextNode("End node"),
                    parents = listOf("start-a", "start-b"),
                ),
            )
        )

}