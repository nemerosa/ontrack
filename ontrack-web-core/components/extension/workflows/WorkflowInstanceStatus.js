import {Space} from "antd";
import {FaCheck, FaSpinner, FaStop, FaTimesCircle} from "react-icons/fa";

export default function WorkflowInstanceStatus({id, status}) {
    return (
        <>
            <Space data-testid={id}>
                {
                    status === 'STARTED' &&
                    <>
                        <FaSpinner color="orange"/>
                        Started
                    </>
                }
                {
                    status === 'RUNNING' &&
                    <>
                        <FaSpinner color="blue"/>
                        Running
                    </>
                }
                {
                    status === 'STOPPED' &&
                    <>
                        <FaStop color="red"/>
                        Stopped
                    </>
                }
                {
                    status === 'ERROR' &&
                    <>
                        <FaTimesCircle color="red"/>
                        Error
                    </>
                }
                {
                    status === 'SUCCESS' &&
                    <>
                        <FaCheck color="green"/>
                        Success
                    </>
                }
            </Space>
        </>
    )
}