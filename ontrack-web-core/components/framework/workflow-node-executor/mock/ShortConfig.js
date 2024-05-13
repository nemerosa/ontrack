import {Typography} from "antd";

export default function MockWorkflowNodeExecutorShortConfig({data}) {
    return (
        <Typography.Text>{data.text}</Typography.Text>
    )
}