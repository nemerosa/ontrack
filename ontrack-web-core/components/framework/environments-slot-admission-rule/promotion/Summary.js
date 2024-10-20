import {Space, Typography} from "antd";

export default function PromotionAdmissionRuleSummary({promotion}) {
    return (
        <>
            <Space>
                <Typography.Text>Promotion needed</Typography.Text>
                <Typography.Text strong>{promotion}</Typography.Text>
            </Space>
        </>
    )
}