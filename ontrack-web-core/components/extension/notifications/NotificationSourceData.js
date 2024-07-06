export default function NotificationSourceData({source}) {
    return (
        <>
            {JSON.stringify(source, null, 2)}
        </>
    )
}