import {Typography} from "antd";

export default function PromotionAdmissionRuleCheck({check, ruleConfig, ruleData}) {
    return (
        <>
            <Typography.Text>Build must be promoted to <b>{ruleConfig.promotion}</b></Typography.Text>
        </>
    )
}