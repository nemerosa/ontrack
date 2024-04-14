import {Descriptions, Typography} from "antd";
import NotificationResultType from "@components/extension/notifications/NotificationResultType";

export default function NotificationRecordResult({channel, result}) {

    const items = [
        {
            key: 'type',
            label: 'Type',
            children: <NotificationResultType type={result.type}/>,
            span: 12,
        },
        {
            key: 'message',
            label: 'Message',
            children: <Typography.Text>{result.message}</Typography.Text>,
            span: 12,
        },
        {
            key: 'output',
            label: 'Output',
            // TODO Rendering according to the channel
            children: JSON.stringify(result.output),
            span: 12,
        },
    ]

    return (
        <>
            <Descriptions items={items} layout="horizontal" column={12}/>
        </>
    )

}