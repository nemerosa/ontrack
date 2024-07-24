import {Typography} from "antd";

export default function MockNotificationChannelConfig({target, data}) {
    return (
        <>
            <Typography.Paragraph>Target: <b>{target}</b></Typography.Paragraph>
            {
                data &&
                <Typography.Paragraph>Data: <b>{data}</b></Typography.Paragraph>
            }
        </>
    )
}