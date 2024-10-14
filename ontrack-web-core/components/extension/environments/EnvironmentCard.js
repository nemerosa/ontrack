import {Card, Space, Tag, Typography} from "antd";

export default function EnvironmentCard({environment}) {
    return (
        <>
            <Card
                data-testid={`environment-${environment.id}`}
                title={environment.name}
                extra={
                    <>
                        <Typography.Text type="secondary"
                                         title="Environment order number">{environment.order}</Typography.Text>
                    </>
                }
            >
                <Space direction="vertical">
                    <div>
                        {
                            environment.tags.map((tag, index) => (
                                <Tag key={index}>{tag}</Tag>
                            ))
                        }
                    </div>
                    <Typography.Text type="secondary">{environment.description}</Typography.Text>
                </Space>
            </Card>
        </>
    )
}