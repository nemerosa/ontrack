import SlotPipelineLink from "@components/extension/environments/SlotPipelineLink";
import {Space} from "antd";

export default function SlotPipelineDeployingWorkflowNodeExecutorOutput({data}) {

    const {pipelineId} = data

    return (
        <>
            <Space direction="vertical" className="ot-line">
                <SlotPipelineLink pipelineId={pipelineId} status={true}/>
            </Space>
        </>
    )
}