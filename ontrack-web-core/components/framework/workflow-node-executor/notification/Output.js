import NotificationRecordOutput from "@components/extension/notifications/NotificationRecordOutput";

export default function NotificationWorkflowNodeExecutorOutput({data, nodeData}) {

    const {channel} = nodeData

    return (
        <>
            <NotificationRecordOutput
                channel={channel}
                output={data}
            />
        </>
    )
}