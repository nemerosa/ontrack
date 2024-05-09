import {Space} from "antd";
import Link from "next/link";

export default function WorkflowNotificationChannelOutput({workflowInstanceId}) {
    return (
        <Space>
            Workflow instance:
            <Link href={`/extension/workflows/instances/${workflowInstanceId}`}>{workflowInstanceId}</Link>
        </Space>
    )
}