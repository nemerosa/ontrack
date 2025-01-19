import Link from "next/link";
import {Button, Space} from "antd";
import {FaProjectDiagram} from "react-icons/fa";
import WorkflowInstanceStatus from "@components/extension/workflows/WorkflowInstanceStatus";

export default function WorkflowInstanceLink({id, workflowInstanceId, status = undefined, name = "Workflow"}) {
    return (
        <>
            {
                workflowInstanceId &&
                <Link
                    href={`/extension/workflows/instances/${workflowInstanceId}`}
                    passHref
                >
                    <Button
                        data-testid={id}
                        data-workflow-instance-id={workflowInstanceId}
                    >
                        <Space>
                            <FaProjectDiagram/>
                            {name}
                            {
                                status &&
                                <WorkflowInstanceStatus status={status}/>
                            }
                        </Space>
                    </Button>
                </Link>
            }
        </>
    )
}