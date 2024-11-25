import {Col, Empty, Row, Space} from "antd";
import EnvironmentCard from "@components/extension/environments/EnvironmentCard";
import SlotCard from "@components/extension/environments/SlotCard";

export default function EnvironmentList({environments}) {
    return (
        <>
            <Space direction="vertical" className="ot-line">
                {
                    environments.length === 0 &&
                    <Empty
                        description="No environment has been created yet."
                    />
                }
                {
                    environments.map(environment => (
                        <Row data-testid={`environment-row-${environment.id}`} gutter={[16, 16]} key={environment.id}
                             wrap={false}>
                            <Col span={4}>
                                <EnvironmentCard environment={environment}/>
                            </Col>
                            {
                                environment.slots.map(slot => (
                                    <Col key={slot.id} span={6}>
                                        <SlotCard slot={slot} showLastDeployed={true}/>
                                    </Col>
                                ))
                            }
                        </Row>
                    ))
                }
            </Space>
        </>
    )
}