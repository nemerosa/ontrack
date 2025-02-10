import NotificationRecordLink from "@components/extension/notifications/NotificationRecordLink";

export default function NotificationRecordTriggerComponent({recordId}) {
    return (
        <>
            <NotificationRecordLink recordId={recordId}/>
        </>
    )
}