import {Space} from "antd";
import {FaBan, FaPlay, FaSpinner, FaThumbsUp, FaTimesCircle} from "react-icons/fa";

export default function SlotPipelineStatus({pipeline, children}) {
    return (
        <>
            <Space>
                {
                    pipeline.status === 'ONGOING' && <>
                        <FaSpinner color="blue"/>
                        Ongoing
                    </>
                }
                {
                    pipeline.status === 'DEPLOYING' && <>
                        <FaPlay color="blue"/>
                        Deploying
                    </>
                }
                {
                    pipeline.status === 'CANCELLED' && <>
                        <FaBan color="gray"/>
                        Cancelled
                    </>
                }
                {
                    pipeline.status === 'ERROR' && <>
                        <FaTimesCircle color="red"/>
                        Error
                    </>
                }
                {
                    pipeline.status === 'DEPLOYED' && <>
                        <FaThumbsUp color="green"/>
                        Deployed
                    </>
                }
                {children}
            </Space>
        </>
    )
}