import {Descriptions} from "antd";
import EventDetails from "@components/core/model/EventDetails";
import NotificationRecordResult from "@components/extension/notifications/NotificationRecordResult";
import NotificationChannelConfig from "@components/extension/notifications/NotificationChannelConfig";

export default function NotificationRecordDetails({record}) {

    const items = [
        {
            key: 'config',
            label: 'Configuration',
            children: <NotificationChannelConfig
                channel={record.channel}
                config={record.channelConfig}
            />,
            span: 6,
        },
        {
            key: 'event',
            label: 'Event',
            children: <EventDetails event={record.event}/>,
            span: 6,
        },
        {
            key: 'result',
            label: 'Result',
            children: <NotificationRecordResult channel={record.channel} result={record.result}/>,
            span: 12,
        },
    ]

    return (
        <>
            <Descriptions items={items} column={12} bordered/>
        </>
    )
}