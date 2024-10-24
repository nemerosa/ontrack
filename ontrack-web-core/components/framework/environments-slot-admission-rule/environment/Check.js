import {Typography} from "antd";

export default function EnvironmentAdmissionRuleCheck({check, ruleConfig, ruleData}) {
    return (
        <>
            <Typography.Text>Build must be <b>deployed</b> in <b>{ruleConfig.environmentName}</b>{ruleConfig.qualifier && ` [{ruleConfig.qualifier}]`}</Typography.Text>
        </>
    )
}