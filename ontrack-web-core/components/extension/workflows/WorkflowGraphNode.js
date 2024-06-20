import {Handle, Position} from "reactflow";
import {Card, Popconfirm, Space, Typography} from "antd";
import {useWorkflowNodeExecutor} from "@components/extension/workflows/WorkflowNodeExecutorContext";
import {FaClock, FaCog, FaPencilAlt, FaTrashAlt, FaWrench} from "react-icons/fa";
import WorkflowNodeExecutorShortConfigWithHelp
    from "@components/extension/workflows/WorkflowNodeExecutorShortConfigWithHelp";
import ConfigureWorkflowNodeDialog, {
    useConfigureWorkflowNodeDialog,
} from "@components/extension/workflows/ConfigureWorkflowNodeDialog";
import Duration from "@components/common/Duration";

export default function WorkflowGraphNode({data}) {

    const {edition, onGraphNodeChange} = data
    const executor = useWorkflowNodeExecutor(data.executorId, [data.executorId])

    const onSuccess = (values /*, context */) => {
        const {id, executorId, timeout, data: nodeData} = values
        const oldId = data.id
        if (onGraphNodeChange) {
            onGraphNodeChange({
                node: {
                    oldId,
                    id,
                    executorId,
                    timeout,
                    data: nodeData,
                },
            })
        }
    }

    const nodeDialog = useConfigureWorkflowNodeDialog({
        onSuccess,
    })

    const configureNode = () => {
        nodeDialog.start({...data})
    }

    const deleteNode = () => {
        const oldId = data.id
        if (onGraphNodeChange) {
            onGraphNodeChange({
                node: {
                    oldId,
                    id: null,
                }
            })
        }
    }

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
                extra={
                    <>
                        {
                            edition &&
                            <Space>
                                <FaPencilAlt
                                    className="ot-action"
                                    title="Configure this node"
                                    onClick={configureNode}
                                />
                                <ConfigureWorkflowNodeDialog dialog={nodeDialog}/>
                                <Popconfirm
                                    title="Delete node"
                                    description="Are you sure to delete this node and all the links to and from this node?"
                                    onConfirm={deleteNode}
                                >
                                    <FaTrashAlt
                                        className="ot-action"
                                        color="red"
                                        title="Delete this node"
                                    />
                                </Popconfirm>
                            </Space>
                        }
                    </>
                }
            >
                {
                    executor &&
                    <Space direction="vertical">
                        <Space>
                            <FaCog/>
                            {
                                !data.executorId &&
                                <Typography.Text italic type="secondary">No executor defined</Typography.Text>
                            }
                            {
                                data.executorId && executor &&
                                <Typography.Text>{executor?.displayName}</Typography.Text>
                            }
                        </Space>
                        <Space>
                            <FaClock title="Timeout"/>
                            <Duration seconds={data.timeout}/>
                        </Space>
                        <Space>
                            <FaWrench/>
                            {
                                !data.executorId &&
                                <Typography.Text italic type="secondary">No executor defined</Typography.Text>
                            }
                            {
                                data.executorId && executor &&
                                <WorkflowNodeExecutorShortConfigWithHelp
                                    executorId={executor.id}
                                    data={data.data}
                                />
                            }
                        </Space>
                    </Space>
                }
            </Card>
        </>
    )
}