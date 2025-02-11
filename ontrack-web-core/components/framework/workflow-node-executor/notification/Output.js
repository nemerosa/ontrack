import NotificationRecordOutput from "@components/extension/notifications/NotificationRecordOutput";
import {Space} from "antd";
import NotificationRecordLink from "@components/extension/notifications/NotificationRecordLink";

export default function NotificationWorkflowNodeExecutorOutput({data, nodeData}) {

    const {channel} = nodeData

    return (
        <>
            <Space direction="vertical">
                {
                    data.result &&
                    <NotificationRecordOutput
                        channel={channel}
                        output={data.result}
                    />
                }
                {/* Link to the notification record */}
                {
                    data.recordId &&
                    <Space>
                        <NotificationRecordLink
                            recordId={data.recordId}
                            text="Notification details"
                        />
                    </Space>
                }
            </Space>
        </>
    )
}