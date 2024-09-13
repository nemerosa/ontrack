import {Descriptions} from "antd";
import {useEffect, useState} from "react";
import NotificationRecordSummary from "@components/extension/notifications/NotificationRecordSummary";

export default function WorkflowInstanceContext({instance}) {

    const [items, setItems] = useState([])

    useEffect(() => {
        const index = {}
        instance.context.data.forEach(item => {
            index[item.key] = item.value
        })

        const items = []

        console.log({index})

        if (index.notificationRecordId && index.notificationRecordId.recordId) {
            items.push({
                key: 'notificationRecord',
                label: "Notification",
                children: <NotificationRecordSummary recordId={index.notificationRecordId.recordId}/>
            })
        }

        setItems(items)
    }, [instance.context.data])

    return (
        <>
            <Descriptions items={items} layout="vertical"/>
        </>
    )
}