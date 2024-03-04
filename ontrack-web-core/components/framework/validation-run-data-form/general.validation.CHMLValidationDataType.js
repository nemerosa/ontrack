import {Col, Form, InputNumber, Row, Typography} from "antd";

export default function CHMLValidationDataType({config}) {
    return (
        <>
            <Row>
                <Col span={12}>
                    <Form.Item
                        name={['data', 'CRITICAL']}
                        label={
                            <Typography.Text type="danger">Critical issues</Typography.Text>
                        }
                    >
                        <InputNumber
                            min={0}
                            style={{width: '5em'}}
                        />
                    </Form.Item>
                </Col>
                <Col span={12}>
                    <Form.Item
                        name={['data', 'HIGH']}
                        label={
                            <Typography.Text type="warning">High issues</Typography.Text>
                        }
                    >
                        <InputNumber
                            min={0}
                            style={{width: '5em'}}
                        />
                    </Form.Item>
                </Col>
            </Row>
            <Row>
                <Col span={12}>
                    <Form.Item
                        name={['data', 'MEDIUM']}
                        label={
                            <Typography.Text style={{color: 'blue'}}>Medium issues</Typography.Text>
                        }
                    >
                        <InputNumber
                            min={0}
                            style={{width: '5em'}}
                        />
                    </Form.Item>
                </Col>
                <Col span={12}>
                    <Form.Item
                        name={['data', 'LOW']}
                        label={
                            <Typography.Text type="secondary">Low issues</Typography.Text>
                        }
                    >
                        <InputNumber
                            min={0}
                            style={{width: '5em'}}
                        />
                    </Form.Item>
                </Col>
            </Row>
        </>
    )
}