import {List, Skeleton, Space, Typography} from "antd";
import {FaCog} from "react-icons/fa";

export default function ListSection({title, icon, loading, items, renderItem}) {
    return (
        <>
            <Space direction="vertical" className="ot-line">
                <Typography.Title level={3}>
                    <Space>
                        {icon}
                        {title}
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