import {Dynamic} from "@components/common/Dynamic";

export default function WorkflowNodeExecutorShortConfig({executorId, data}) {
    return <Dynamic
        path={`framework/workflow-node-executor/${executorId}/ShortConfig`}
        props={{data}}
    />
}