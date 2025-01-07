import {Typography} from "antd";

export default function PromotionAdmissionRuleSummary({promotion}) {
    return (
        <>
            <Typography.Text strong>{promotion}</Typography.Text> promotion is required
        </>
    )
}