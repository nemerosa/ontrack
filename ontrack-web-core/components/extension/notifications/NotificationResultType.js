import {Space, Spin} from "antd";
import {FaCheck, FaExclamationTriangle, FaTimesCircle} from "react-icons/fa";

export const notificationResultTypes = [
    {
        key: 'OK',
        name: 'OK',
        color: 'green',
        icon: <FaCheck color="green"/>,
    },
    {
        key: 'ONGOING',
        name: 'Ongoing',
        color: 'green',
        icon: <Spin size="small"/>,
    },
    {
        key: 'NOT_CONFIGURED',
        name: 'Not configured',
        color: 'orange',
        icon: <FaExclamationTriangle color="orange"/>,
    },
    {
        key: 'INVALID_CONFIGURATION',
        name: 'Invalid configuration',
        color: 'orange',
        icon: <FaExclamationTriangle color="orange"/>,
    },
    {
        key: 'DISABLED',
        name: 'Disabled',
        color: 'orange',
        icon: <FaExclamationTriangle color="orange"/>,
    },
    {
        key: 'ERROR',
        name: 'Error',
        color: 'red',
        icon: <FaTimesCircle color="red"/>,
    },
    {
        key: 'ASYNC',
        name: 'Async',
        color: 'green',
        icon: <Spin size="small"/>,
    },
]

export default function NotificationResultType({type}) {

    const typeObject = notificationResultTypes.find(it => it.key === type)

    return (
        <>
            {
                typeObject && <Space>
                    {typeObject.icon}
                    {typeObject.name}
                </Space>
            }
        </>
    )
}