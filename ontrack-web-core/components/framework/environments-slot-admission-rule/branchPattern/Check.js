import {Space, Tag, Typography} from "antd";

export default function EnvironmentAdmissionRuleCheck({check, ruleConfig, ruleData}) {
    return (
        <>
            <Typography.Text>Build branch name must be in:</Typography.Text>
            <ul>
                {
                    ruleConfig.includes.length > 0 &&
                    <li>
                        <Space>
                            Inclusions:
                            {
                                ruleConfig.includes.map((regex, index) => <Tag key={index}>{regex}</Tag>)
                            }
                        </Space>
                    </li>
                }
                {
                    ruleConfig.excludes && ruleConfig.excludes.length > 0 &&
                    <li>
                        <Space>
                            Exclusions
                            {
                                ruleConfig.excludes.map((regex, index) => <Tag key={index}>{regex}</Tag>)
                            }
                        </Space>
                    </li>
                }
            </ul>
        </>
    )
}