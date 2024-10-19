import {Descriptions} from "antd";
import YesNo from "@components/common/YesNo";
import SlotPipelineDeploymentStatusChecks from "@components/extension/environments/SlotPipelineDeploymentStatusChecks";

export default function SlotPipelineDeploymentStatus({deploymentStatus}) {

    const items = [
        {
            key: 'status',
            label: 'Deployable',
            span: 12,
            children: <YesNo value={deploymentStatus.status}/>,
        },
        {
            key: 'override',
            label: 'Overridden',
            span: 12,
            children: <YesNo value={deploymentStatus.override}/>,
        },
        {
            key: 'checks',
            label: 'Checks',
            span: 12,
            children: <SlotPipelineDeploymentStatusChecks checks={deploymentStatus.checks}/>,
        },
    ]

    return (
        <>
            <Descriptions
                items={items}
                layout="horizontal"
            />
        </>
    )
}