import {Form, Input, Space, Typography} from "antd";
import SelectMessageType from "@components/common/SelectMessageType";
import MessageType from "@components/common/MessageType";

export default function SlackNotificationChannelConfig({channel, type}) {
    return (
        <>
            <Space>
                Channel:
                <Typography.Text code>{channel}</Typography.Text>
                <Typography.Text>(<MessageType value={type}/>)</Typography.Text>
            </Space>
        </>
    )
}