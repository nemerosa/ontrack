import {Space, Tag, Typography} from "antd";

export default function ThresholdNumberValidationDataType({warningThreshold, failureThreshold, okIfGreater}) {
    return (
        <>
            {
                okIfGreater &&
                <Space>
                    <Typography.Text type="danger">Failed</Typography.Text>
                    &lt;
                    <Tag>{failureThreshold}</Tag>
                    &le;
                    <Typography.Text type="warning">Warning</Typography.Text>
                    &lt;
                    <Tag>{warningThreshold}</Tag>
                    &le;
                    <Typography.Text type="success">Passed</Typography.Text>
                </Space>
            }
            {
                !okIfGreater &&
                <Space>
                    <Typography.Text type="success">Passed</Typography.Text>
                    &le;
                    <Tag>{warningThreshold}</Tag>
                    &lt;
                    <Typography.Text type="warning">Warning</Typography.Text>
                    &le;
                    <Tag>{failureThreshold}</Tag>
                    &lt;
                    <Typography.Text type="danger">Failed</Typography.Text>
                </Space>
            }
        </>
    )
}