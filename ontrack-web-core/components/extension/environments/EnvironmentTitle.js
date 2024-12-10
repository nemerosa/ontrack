import {Space, Typography} from "antd";
import EnvironmentIcon from "@components/extension/environments/EnvironmentIcon";

export default function EnvironmentTitle({environment, icon = true}) {
    return (
        <>
            <Space>
                {
                    icon &&
                    <EnvironmentIcon environment={environment}/>
                }
                <Typography.Text>{environment.name}</Typography.Text>
            </Space>
        </>
    )
}