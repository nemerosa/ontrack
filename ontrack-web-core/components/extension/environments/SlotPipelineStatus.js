import {Space} from "antd";
import {FaBan, FaPlay, FaSpinner, FaThumbsUp} from "react-icons/fa";

export default function SlotPipelineStatus({pipeline, showText = true, children}) {
    return (
        <>
            <Space data-testid={`pipeline-actions-${pipeline.id}`}>
                {
                    pipeline.status === 'ONGOING' && <>
                        <FaSpinner color="blue"/>
                        { showText && "Pending" }
                    </>
                }
                {
                    pipeline.status === 'DEPLOYING' && <>
                        <FaPlay color="blue"/>
                        { showText && "Deploying" }
                    </>
                }
                {
                    pipeline.status === 'CANCELLED' && <>
                        <FaBan color="gray"/>
                        { showText && "Cancelled" }
                    </>
                }
                {
                    pipeline.status === 'DEPLOYED' && <>
                        <FaThumbsUp color="green"/>
                        { showText && "Deployed" }
                    </>
                }
                {children}
            </Space>
        </>
    )
}