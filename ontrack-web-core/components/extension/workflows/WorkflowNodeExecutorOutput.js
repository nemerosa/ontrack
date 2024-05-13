import {Dynamic} from "@components/common/Dynamic";

export default function WorkflowNodeExecutorOutput({executorId, data, nodeData}) {
    return <Dynamic
        path={`framework/workflow-node-executor/${executorId}/Output`}
        props={{data, nodeData}}
    />
}