import {Space} from "antd";
import MessageTypeIcon from "@components/common/MessageTypeIcon";

export const messageTypes = [
    'SUCCESS',
    'INFO',
    'WARNING',
    'ERROR',
]

export const messageTypeNames = {
    SUCCESS: "Success",
    INFO: "Information",
    WARNING: "Warning",
    ERROR: "Error",
}

export default function MessageType({value}) {
    return (
        <Space>
            <MessageTypeIcon type={value}/>
            {messageTypeNames[value]}
        </Space>
    )
}