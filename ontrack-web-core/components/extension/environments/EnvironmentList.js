import {Col, Row, Space} from "antd";
import EnvironmentCard from "@components/extension/environments/EnvironmentCard";

export default function EnvironmentList({environments}) {
    return (
        <>
            <Space direction="vertical" className="ot-line">
                {
                    environments.map(environment => (
                        <Row gutter={[16, 16]} key={environment.id}>
                            <Col span={6}>
                                <EnvironmentCard environment={environment}/>
                            </Col>
                        </Row>
                    ))
                }
            </Space>
        </>
    )
}