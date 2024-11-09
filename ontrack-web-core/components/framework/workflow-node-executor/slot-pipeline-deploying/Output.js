import SlotPipelineLink from "@components/extension/environments/SlotPipelineLink";
import {Space} from "antd";
import SlotPipelineDeploymentStatusChecks from "@components/extension/environments/SlotPipelineDeploymentStatusChecks";

export default function SlotPipelineDeployingWorkflowNodeExecutorOutput({data}) {

    const {pipelineId} = data

    return (
        <>
            <Space direction="vertical" className="ot-line">
                <SlotPipelineLink pipelineId={pipelineId} status={true}/>
                <SlotPipelineDeploymentStatusChecks pipeline={{id: pipelineId}}/>
            </Space>
        </>
    )
}