import {Descriptions} from "antd";
import EventDetails from "@components/core/model/EventDetails";

export default function NotificationRecordDetails({record}) {

    const items = [
        {
            key: 'event',
            label: 'Event',
            children: <EventDetails event={record.event}/>
        }
    ]

    return (
        <>
            <Descriptions items={items}/>
        </>
    )
}