import {List, Skeleton, Space, Typography} from "antd";

export default function ListSection({title, extraTitle, icon, loading, items}) {
    return (
        <>
            <Space direction="vertical" className="ot-line">
                <Typography.Title level={3}>
                    <Space>
                        {icon}
                        {title}
                        {extraTitle}
                    </Space>
                </Typography.Title>
                <Skeleton active loading={loading}>
                    <List
                        itemLayout="horizontal"
                        dataSource={items}
                        renderItem={(item) =>
                            <List.Item>
                                <List.Item.Meta
                                    avatar={item.icon}
                                    title={item.title}
                                    description={item.content}
                                />
                            </List.Item>
                        }
                    />
                </Skeleton>
            </Space>
        </>
    )
}