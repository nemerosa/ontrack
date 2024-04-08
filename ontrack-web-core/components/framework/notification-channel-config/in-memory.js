import {Space, Typography} from "antd";

export default function InMemoryNotificationChannelConfig({group}) {
    return (
        <>
            <Space>
                Group:
                <Typography.Text code>{group}</Typography.Text>
            </Space>
        </>
    )
}