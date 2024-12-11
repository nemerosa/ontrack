import {Space, Typography} from "antd";
import EnvironmentEditableIcon from "@components/extension/environments/EnvironmentEditableIcon";

export default function EnvironmentTitle({environment, icon = true}) {
    return (
        <>
            <Space>
                {
                    icon &&
                    <EnvironmentEditableIcon environment={environment}/>
                }
                <Typography.Text>{environment.name}</Typography.Text>
            </Space>
        </>
    )
}