import {Descriptions} from "antd";
import EventDetails from "@components/core/model/EventDetails";
import NotificationRecordResult from "@components/extension/notifications/NotificationRecordResult";

export default function NotificationRecordDetails({record}) {

    const items = [
        {
            key: 'event',
            label: 'Event',
            children: <EventDetails event={record.event}/>,
            span: 12,
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
            <Descriptions items={items} column={12}/>
        </>
    )
}