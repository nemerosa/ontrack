import {ReactFlowProvider} from "reactflow";
import WorkflowGraphFlow from "@components/extension/workflows/WorkflowGraphFlow";
import WorkflowNodeExecutorContextProvider from "@components/extension/workflows/WorkflowNodeExecutorContext";

export default function WorkflowGraph({workflowNodes}) {
    return (
        <>
            <WorkflowNodeExecutorContextProvider>
                <ReactFlowProvider>
                    <WorkflowGraphFlow workflowNodes={workflowNodes}/>
                </ReactFlowProvider>
            </WorkflowNodeExecutorContextProvider>
        </>
    )
}