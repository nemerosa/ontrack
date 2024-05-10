import {Handle, Position} from "reactflow";
import {Card, Space, Typography} from "antd";
import {useWorkflowNodeExecutor} from "@components/extension/workflows/WorkflowNodeExecutorContext";
import {FaCog, FaWrench} from "react-icons/fa";
import WorkflowNodeExecutorShortConfigWithHelp
    from "@components/extension/workflows/WorkflowNodeExecutorShortConfigWithHelp";

export default function WorkflowGraphNode({data}) {

    const executor = useWorkflowNodeExecutor(data.executorId, [data.executorId])

    return (
        <>
            <Handle type="target" position={Position.Left}/>
            <Handle type="source" position={Position.Right}/>

            <Card
                title={data.id}
                size="small"
                bodyStyle={{
                    overflow: 'hidden'
                }}
            >
                {
                    executor &&
                    <Space direction="vertical">
                        <Space>
                            <FaCog/>
                            <Typography.Text>{executor?.displayName}</Typography.Text>
                        </Space>
                        <Space>
                            <FaWrench/>
                            <WorkflowNodeExecutorShortConfigWithHelp
                                executorId={executor.id}
                                data={data.data}
                            />
                        </Space>
                    </Space>
                }
            </Card>
        </>
    )
}