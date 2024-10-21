import {Popover} from "antd";
import {FaInfoCircle} from "react-icons/fa";
import SlotPipelineDeploymentStatus from "@components/extension/environments/SlotPipelineDeploymentStatus";

export default function SlotPipelineDeploymentStatusButton({pipeline}) {
    return (
        <>
            <Popover
                title={`Pipeline #${pipeline.number}`}
                content={
                    <SlotPipelineDeploymentStatus
                        pipeline={pipeline}
                    />
                }
                overlayStyle={{
                    width: '75%',
                }}
            >
                <FaInfoCircle className="ot-action"/>
            </Popover>
        </>
    )
}