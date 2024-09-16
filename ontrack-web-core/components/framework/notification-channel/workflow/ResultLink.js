import Link from "next/link";
import {Button, Space} from "antd";
import {FaProjectDiagram} from "react-icons/fa";

export default function ResultLink({workflowInstanceId}) {
    return (
        <>
            {
                workflowInstanceId &&
                <Link href={`/extension/workflows/instances/${workflowInstanceId}`} passHref>
                    <Button>
                        <Space>
                            <FaProjectDiagram/>
                            Workflow
                        </Space>
                    </Button>
                </Link>
            }
        </>
    )
}