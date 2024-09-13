import {Descriptions} from "antd";
import EventDetails from "@components/core/model/EventDetails";
import NotificationRecordResult from "@components/extension/notifications/NotificationRecordResult";
import NotificationChannelConfig from "@components/extension/notifications/NotificationChannelConfig";
import TimestampText from "@components/common/TimestampText";
import NotificationSourceData from "@components/extension/notifications/NotificationSourceData";
import EventEntity from "@components/core/model/EventEntity";

export default function NotificationRecordDetails({record, includeAll = false}) {

    const items = []

    if (includeAll) {
        items.push(
            {
                key: 'id',
                label: "ID",
                children: record.id,
            },
            {
                key: 'timestamp',
                label: "Timestamp",
                children: <TimestampText
                    value={record.timestamp}
                    format="YYYY MMM DD, HH:mm:ss"
                />,
            },
            {
                key: 'channel',
                label: "Channel",
                children: record.channel,
            },
            {
                key: 'source',
                label: "Source",
                children: <NotificationSourceData source={record.source}/>
            },
            {
                key: 'entity',
                label: "Entity",
                children: <EventEntity event={record.event}/>
            },
        )
    }

    items.push(
        {
            key: 'config',
            label: 'Configuration',
            children: <NotificationChannelConfig
                channel={record.channel}
                config={record.channelConfig}
            />,
        },
        {
            key: 'event',
            label: 'Event',
            children: <EventDetails event={record.event}/>,
        },
        {
            key: 'result',
            label: 'Result',
            children: <NotificationRecordResult channel={record.channel} result={record.result}/>,
        },
    )

    return (
        <>
            <Descriptions items={items} column={1} bordered/>
        </>
    )
}