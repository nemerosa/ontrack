import {Space} from "antd";
import {FaCheckCircle, FaSpinner, FaTimesCircle} from "react-icons/fa";

export default function WorkflowInstanceStatus({status}) {
    return (
        <>
            {
                status === 'STARTED' &&
                <Space>
                    <FaSpinner color="orange"/>
                    Started
                </Space>
            }
            {
                status === 'RUNNING' &&
                <Space>
                    <FaSpinner color="blue"/>
                    Running
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