import {Typography} from "antd";

export default function NotificationWorkflowNodeExecutorShortConfig({data}) {

    const {channel} = data

    return (
        <>
            <Typography.Text>{channel}</Typography.Text>
        </>
    )

}