import {Popover} from "antd";
import {FaInfoCircle} from "react-icons/fa";
import SlotPipelineDeploymentStatus from "@components/extension/environments/SlotPipelineDeploymentStatus";

export default function SlotPipelineDeploymentStatusButton({deploymentStatus}) {
    return (
        <>
            <Popover
                title="Deployment Status"
                content={
                    <SlotPipelineDeploymentStatus
                        deploymentStatus={deploymentStatus}
                    />
                }
                overlayStyle={{
                    width: '33%',
                }}
            >
                <FaInfoCircle className="ot-action"/>
            </Popover>
        </>
    )
}