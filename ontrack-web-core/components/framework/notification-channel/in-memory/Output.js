import {Descriptions, Typography} from "antd";
import CheckIcon from "@components/common/CheckIcon";

export default function InMemoryNotificationChannelOutput({sent, data}) {
    return (
        <>
            <Descriptions items={[
                {
                    key: 'sent',
                    label: 'Sent',
                    children: <CheckIcon value={sent}/>,
                },
                {
                    key: 'data',
                    label: 'Data',
                    children: <Typography.Text code>{data}</Typography.Text>,
                },
            ]}/>
        </>
    )
}