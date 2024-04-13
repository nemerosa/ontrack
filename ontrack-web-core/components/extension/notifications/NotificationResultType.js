import {Space} from "antd";
import {FaCheck, FaExclamationTriangle, FaTimesCircle} from "react-icons/fa";

export default function NotificationResultType({type}) {
    return (
        <>
            {type === 'OK' && <Space>
                <FaCheck color="green"/>
                OK
            </Space>}
            {type === 'NOT_CONFIGURED' && <Space>
                <FaExclamationTriangle color="orange"/>
                Not configured
            </Space>}
            {type === 'INVALID_CONFIGURATION' && <Space>
                <FaExclamationTriangle color="orange"/>
                Invalid configuration
            </Space>}
            {type === 'DISABLED' && <Space>
                <FaExclamationTriangle color="orange"/>
                Disabled
            </Space>}
            {type === 'ERROR' && <Space>
                <FaTimesCircle color="red"/>
                Error
            </Space>}
        </>
    )
}