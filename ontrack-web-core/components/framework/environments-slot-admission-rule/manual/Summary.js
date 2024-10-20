import {Space, Typography} from "antd";

export default function ManualAdmissionRuleSummary({message}) {
    return (
        <>
            <Space>
                <Typography.Text>Manual approval</Typography.Text>
                <Typography.Text italic>{message}</Typography.Text>
            </Space>
        </>
    )
}