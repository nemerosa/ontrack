import {Alert, Empty, Space, Typography} from "antd";
import {FaCog, FaPlay, FaStop, FaStopwatch, FaTag, FaWrench} from "react-icons/fa";
import WorkflowInstanceNodeStatus from "@components/extension/workflows/WorkflowInstanceNodeStatus";
import TimestampText from "@components/common/TimestampText";
import DurationMs from "@components/common/DurationMs";
import {useWorkflowNodeExecutor} from "@components/extension/workflows/WorkflowNodeExecutorContext";
import WorkflowNodeExecutorConfig from "@components/extension/workflows/WorkflowNodeExecutorConfig";
import WorkflowNodeExecutorOutput from "@components/extension/workflows/WorkflowNodeExecutorOutput";

export default function WorkflowInstanceGraphInfo({instance, selectedNode}) {

    const executor = useWorkflowNodeExecutor(selectedNode?.workflowNode?.executorId, [selectedNode])

    return (
        <>
            <div
                style={{
                    padding: '0.5em',
                }}
            >
                {
                    !selectedNode &&
                    <Typography.Text italic>Click on a node to display information.</Typography.Text>
                }
                {
                    selectedNode &&
                    <Space direction="vertical" className="ot-line">
                        <Space>
                            <FaTag/>
                            <Typography.Text strong>{selectedNode.workflowNode.id}</Typography.Text>
                        </Space>
                        <WorkflowInstanceNodeStatus status={selectedNode.nodeExecution.status}/>
                        <Space>
                            <FaPlay/>
                            <TimestampText value={selectedNode.nodeExecution.startTime}/>
                            {
                                selectedNode.nodeExecution.endTime &&
                                <>
                                    <FaStop/>
                                    <TimestampText value={selectedNode.nodeExecution.endTime}/>
                                </>
                            }
                        </Space>
                        {
                            selectedNode.nodeExecution.endTime &&
                            <Space>
                                <FaStopwatch/>
                                <DurationMs ms={selectedNode.nodeExecution.durationMs}/>
                            </Space>
                        }
                        {/* Execution ID and configuration */}
                        <Typography.Text strong italic
                                         className="ot-workflow-node-info-title">Execution</Typography.Text>
                        {
                            executor &&
                            <>
                                <Space>
                                    <FaCog/>
                                    <Typography.Text>{executor?.displayName}</Typography.Text>
                                    (<Typography.Text code>{executor?.id}</Typography.Text>)
                                </Space>
                                <Space>
                                    <FaWrench/>
                                    <div>
                                        <Typography.Text italic>Configuration</Typography.Text>
                                        <div>
                                            <WorkflowNodeExecutorConfig executorId={executor.id}
                                                                        data={selectedNode.workflowNode.data}/>
                                        </div>
                                    </div>
                                </Space>
                            </>
                        }
                        {/* Error */}
                        {
                            selectedNode.nodeExecution.status === 'ERROR' &&
                            <>
                                <Typography.Text strong italic
                                                 className="ot-workflow-node-info-title">Error</Typography.Text>
                                <Alert
                                    type="error"
                                    message={selectedNode.nodeExecution.error}
                                />
                            </>
                        }
                        {/* Execution ID and output */}
                        <Typography.Text strong italic className="ot-workflow-node-info-title">Output</Typography.Text>
                        {
                            selectedNode.nodeExecution.output &&
                            <WorkflowNodeExecutorOutput executorId={executor.id}
                                                        nodeData={selectedNode.workflowNode.data}
                                                        data={selectedNode.nodeExecution.output}/>
                        }
                        {
                            !selectedNode.nodeExecution.output &&
                            <Empty description="No output"/>
                        }
                    </Space>
                }
            </div>
        </>
    )
}