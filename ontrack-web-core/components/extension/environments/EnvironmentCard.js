import {Card, Space, Tag, Typography} from "antd";
import EnvironmentOrder from "@components/extension/environments/EnvironmentOrder";
import EnvironmentTitle from "@components/extension/environments/EnvironmentTitle";

export default function EnvironmentCard({environment}) {
    return (
        <>
            <Card
                style={{
                    height: '100%',
                }}
                size="small"
                data-testid={`environment-${environment.id}`}
                title={
                    <EnvironmentTitle environment={environment}/>
                }
                extra={
                    <>
                        <EnvironmentOrder order={environment.order}/>
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