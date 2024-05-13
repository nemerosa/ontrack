import {Dynamic} from "@components/common/Dynamic";

export default function WorkflowNodeExecutorConfig({executorId, data}) {
    return <Dynamic
        path={`framework/workflow-node-executor/${executorId}/Config`}
        props={{data}}
    />
}