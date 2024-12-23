import {Space, Tag, Typography} from "antd";
import EnvironmentEditableIcon from "@components/extension/environments/EnvironmentEditableIcon";

export default function EnvironmentTitle({environment, icon = true, tags = true, editable = true}) {
    return (
        <>
            <Space>
                {
                    icon &&
                    <EnvironmentEditableIcon editable={editable} environment={environment}/>
                }
                <Typography.Text>{environment.name}</Typography.Text>
                {
                    tags && <Space size={0}>
                        {
                            environment.tags.map((tag, index) => (
                                <Tag key={index}>{tag}</Tag>
                            ))
                        }
                    </Space>
                }
            </Space>
        </>
    )
}