import {FaBan, FaPlay, FaSpinner, FaThumbsUp} from "react-icons/fa";
import {Space} from "antd";

export default function SlotPipelineStatusIcon({status, showText = true}) {
    return (
        <>
            <Space>
                {
                    status === 'CANDIDATE' && <>
                        <FaSpinner color="blue"/>
                        {showText && "Candidate"}
                    </>
                }
                {
                    status === 'RUNNING' && <>
                        <FaPlay color="blue"/>
                        {showText && "Running"}
                    </>
                }
                {
                    status === 'CANCELLED' && <>
                        <FaBan color="gray"/>
                        {showText && "Cancelled"}
                    </>
                }
                {
                    status === 'DONE' && <>
                        <FaThumbsUp color="green"/>
                        {showText && "Done"}
                    </>
                }
            </Space>
        </>
    )
}