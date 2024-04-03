import {List, Space, Tag, Typography} from "antd";
import Link from "next/link";

export default function Display({property}) {

    return (
        <>
            <List
                itemLayout="horizontal"
                dataSource={property.value.items}
                renderItem={(item, index) => (
                    <List.Item>
                        <List.Item.Meta
                            title={
                                <Space>
                                    {
                                        item.category &&
                                        <Tag>{item.category}</Tag>
                                    }
                                    {item.name}
                                </Space>
                            }
                        />
                        {
                            !item.link &&
                            <Typography.Text>{item.value}</Typography.Text>
                        }
                        {
                            item.link &&
                            <Link href={item.link}>{item.value}</Link>
                        }
                    </List.Item>
                )}
            />
        </>
    )
}