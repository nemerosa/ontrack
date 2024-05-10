import WorkflowGraphFlow from "@components/extension/workflows/WorkflowGraphFlow";
import WorkflowNodeExecutorContextProvider from "@components/extension/workflows/WorkflowNodeExecutorContext";

export default function WorkflowGraph({workflowNodes, edition = false}) {
    return (
        <>
            <WorkflowNodeExecutorContextProvider>
                <WorkflowGraphFlow workflowNodes={workflowNodes} edition={edition}/>
            </WorkflowNodeExecutorContextProvider>
        </>
    )
}