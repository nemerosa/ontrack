import {Dynamic} from "@components/common/Dynamic";

export default function WorkflowNodeExecutorForm({executorId}) {
    return <Dynamic
        path={`framework/workflow-node-executor/${executorId}/Form`}
    />
}