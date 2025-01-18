import {Space, Typography} from "antd";

export default function CascSchemaNode({name, type, description, required}) {
    return (
        <>
            <Space>
                <Typography.Text code>{name}</Typography.Text>
                {
                    type &&
                    <Typography.Text strong>{type}</Typography.Text>
                }
                {
                    required &&
                    <Typography.Text strong>(required)</Typography.Text>
                }
                <Typography.Text type="secondary">{description}</Typography.Text>
            </Space>
        </>
    )
}