import {Button, Space, Typography} from "antd";
import {FaProjectDiagram} from "react-icons/fa";
import ShowWorkflowInstanceStatus from "@components/extension/workflows/ShowWorkflowInstanceStatus";

export default function WorkflowNotificationSource({workflowInstanceId}) {
    return (
        <>
            <Button href={`/extension/workflows/instances/${workflowInstanceId}`}>
                <Space>
                    <FaProjectDiagram/>
                    <Typography.Text>Workflow</Typography.Text>
                    <ShowWorkflowInstanceStatus instanceId={workflowInstanceId}/>
                </Space>
            </Button>
        </>

    )
}