import {Space} from "antd";
import {FaCheckCircle, FaHourglass, FaSpinner, FaStop, FaTimesCircle} from "react-icons/fa";

export default function WorkflowInstanceNodeStatus({status}) {
    return (
        <>
            {
                status === 'CREATED' &&
                <Space>
                    <FaSpinner color="gray" className="anticon-spin"/>
                    Idle
                </Space>
            }
            {
                status === 'WAITING' &&
                <Space>
                    <FaHourglass color="blue" className="anticon-spin"/>
                    Waiting
                </Space>
            }
            {
                status === 'STARTED' &&
                <Space>
                    <FaSpinner color="green" className="anticon-spin"/>
                    Started
                </Space>
            }
            {
                status === 'CANCELLED' &&
                <Space>
                    <FaStop color="red"/>
                    Stopped
                </Space>
            }
            {
                status === 'ERROR' &&
                <Space>
                    <FaTimesCircle color="red"/>
                    Error
                </Space>
            }
            {
                status === 'SUCCESS' &&
                <Space>
                    <FaCheckCircle color="green"/>
                    Success
                </Space>
            }
        </>
    )
}