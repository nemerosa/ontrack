import {Space, Tag, Typography} from "antd";

export default function EnvironmentName({environment}) {
    return (
        <>
            <Space>
                <Typography.Text>{environment.name}</Typography.Text>
                {
                    environment.tags.length > 0 && <>
                        {
                            environment.tags.map((tag, index) => (
                                <Tag key={index}>{tag}</Tag>
                            ))
                        }
                    </>
                }
            </Space>
        </>
    )
}