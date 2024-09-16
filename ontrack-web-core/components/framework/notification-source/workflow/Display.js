import {Button, Space, Typography} from "antd";
import {FaProjectDiagram} from "react-icons/fa";
import ShowWorkflowInstanceStatus from "@components/extension/workflows/ShowWorkflowInstanceStatus";
import Link from "next/link";

export default function WorkflowNotificationSource({workflowInstanceId}) {
    return (
        <>
            <Link href={`/extension/workflows/instances/${workflowInstanceId}`} passHref>
                <Button>
                    <Space>
                        <FaProjectDiagram/>
                        <Typography.Text>Workflow</Typography.Text>
                        <ShowWorkflowInstanceStatus instanceId={workflowInstanceId}/>
                    </Space>
                </Button>
            </Link>
        </>

    )
}