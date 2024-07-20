import {Popover, Space, Typography} from "antd";
import {FaInfoCircle} from "react-icons/fa";
import WorkflowNodeInfo from "@components/extension/workflows/WorkflowNodeInfo";

export default function WorkflowNodeTitle({node}) {
    return (
        <>
            <Popover
                content={
                    <div style={{maxWidth: 600, width: 'auto'}}>
                        <WorkflowNodeInfo
                            node={node}
                        />
                    </div>
                }
            >
                <Space>
                    <Typography.Text>{node.id}</Typography.Text>
                    <FaInfoCircle color="blue"/>
                </Space>
            </Popover>
        </>
    )
}