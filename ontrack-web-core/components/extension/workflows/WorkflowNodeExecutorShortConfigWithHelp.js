import {Popover, Space} from "antd";
import WorkflowNodeExecutorShortConfig from "@components/extension/workflows/WorkflowNodeExecutorShortConfig";
import WorkflowNodeExecutorConfig from "@components/extension/workflows/WorkflowNodeExecutorConfig";
import {FaInfoCircle} from "react-icons/fa";

export default function WorkflowNodeExecutorShortConfigWithHelp({executorId, data}) {
    return (
        <Popover
            content={
                <div style={{maxWidth: 600, width: 'auto'}}>
                    <WorkflowNodeExecutorConfig
                        executorId={executorId}
                        data={data}
                    />
                </div>
            }
        >
            <Space>
                <WorkflowNodeExecutorShortConfig
                    executorId={executorId}
                    data={data}
                />
                <FaInfoCircle color="blue"/>
            </Space>
        </Popover>
    )
}