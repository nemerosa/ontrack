import {Space, Typography} from "antd";

export default function EnvironmentAdmissionRuleSummary({environmentName, qualifier}) {
    return (
        <>
            <Space>
                <Typography.Text>
                    Must be deployed in <b>{environmentName}{qualifier && ` ’[${qualifier}]`}</b>
                </Typography.Text>
            </Space>
        </>
    )
}