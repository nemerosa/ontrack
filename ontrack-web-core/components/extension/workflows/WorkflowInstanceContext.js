import {Descriptions} from "antd";
import {useEffect, useState} from "react";
import NotificationRecordSummary from "@components/extension/notifications/NotificationRecordSummary";

export default function WorkflowInstanceContext({instance}) {

    const [items, setItems] = useState([])

    useEffect(() => {
        const index = {}
        instance.event.values.forEach((name, value) => {
            index[name] = value
        })

        const items = []

        if (index.notificationRecordId) {
            items.push({
                key: 'notificationRecord',
                label: "Notification",
                children: <NotificationRecordSummary recordId={index.notificationRecordId}/>
            })
        }

        setItems(items)
    }, [instance.event.values])

    return (
        <>
            <Descriptions items={items} layout="vertical"/>
        </>
    )
}