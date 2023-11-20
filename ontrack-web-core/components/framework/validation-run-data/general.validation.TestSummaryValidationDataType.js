import {Space, Typography} from "antd";

export default function TestSummaryValidationDataType({passed, skipped, failed, total}) {
    return (
        <Space>
            <span>Passed: <Typography.Text strong>{passed}</Typography.Text></span>
            <span>Skipped: <Typography.Text strong>{skipped}</Typography.Text></span>
            <span>Failed: <Typography.Text strong>{failed}</Typography.Text></span>
            <span>Total: <Typography.Text strong>{total}</Typography.Text></span>
        </Space>
    )
}