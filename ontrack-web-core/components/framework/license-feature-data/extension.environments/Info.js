import {Space, Typography} from "antd";
import EnvironmentsCount from "@components/extension/environments/EnvironmentsCount";

export default function EnvironmentInfo({maxEnvironments}) {
    return (
        <>
            {
                Number(maxEnvironments) <= 0 && "No limit in number of environments"
            }
            {
                Number(maxEnvironments) > 0 && <Space>
                    <Typography.Text>Max. environments {maxEnvironments}</Typography.Text>
                    <Typography.Text>(currently using <EnvironmentsCount/>)</Typography.Text>
                </Space>
            }
        </>
    )
}