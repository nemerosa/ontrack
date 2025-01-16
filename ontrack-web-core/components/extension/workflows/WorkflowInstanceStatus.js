import {Space} from "antd";
import {FaCheck, FaSpinner, FaStop, FaTimesCircle} from "react-icons/fa";

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
                    <FaCheck color="green"/>
                    Success
                </Space>
            }
        </>
    )
}