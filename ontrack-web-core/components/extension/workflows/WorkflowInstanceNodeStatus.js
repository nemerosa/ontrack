import {Space} from "antd";
import {FaCheckCircle, FaSpinner, FaStop, FaTimesCircle} from "react-icons/fa";

export default function WorkflowInstanceNodeStatus({status}) {
    return (
        <>
            {
                status === 'IDLE' &&
                <Space>
                    <FaSpinner color="green"/>
                    Idle
                </Space>
            }
            {
                status === 'STARTED' &&
                <Space>
                    <FaSpinner color="blue"/>
                    Started
                </Space>
            }
            {
                status === 'STOPPED' &&
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