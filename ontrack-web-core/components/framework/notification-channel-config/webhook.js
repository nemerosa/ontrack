import {Space, Typography} from "antd";

export default function WebhookNotificationChannelConfig({name}) {
    return (
        <>
            <Space>
                Name:
                <Typography.Text code>{name}</Typography.Text>
            </Space>
        </>
    )
}