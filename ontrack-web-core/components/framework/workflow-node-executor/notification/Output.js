import NotificationRecordOutput from "@components/extension/notifications/NotificationRecordOutput";
import {Space} from "antd";

export default function NotificationWorkflowNodeExecutorOutput({data, nodeData}) {

    const {channel} = nodeData

    return (
        <>
            <Space direction="vertical">
                <NotificationRecordOutput
                    channel={channel}
                    output={data}
                />
                {/* TODO Link to the notification record */}
            </Space>
        </>
    )
}