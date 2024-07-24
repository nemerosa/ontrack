import {Typography} from "antd";

export default function MockNotificationChannelOutput({target, data}) {
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