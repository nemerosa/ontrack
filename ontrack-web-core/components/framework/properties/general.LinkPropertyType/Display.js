import {List, Space, Tag, Typography} from "antd";

export default function Display({property}) {

    return (
        <>
            <List
                itemLayout="vertical"
                dataSource={property.value.links}
                size="small"
                renderItem={(link) => (
                    <List.Item>
                        <Space>
                            <Tag>{link.name}</Tag>
                            <Typography.Link href={link.value}>{link.value}</Typography.Link>
                        </Space>
                    </List.Item>
                )}
            />
        </>
    )
}