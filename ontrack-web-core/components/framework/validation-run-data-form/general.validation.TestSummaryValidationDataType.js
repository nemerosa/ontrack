import {Form, InputNumber, Space, Typography} from "antd";
import {FaCheck, FaExclamationCircle, FaExclamationTriangle} from "react-icons/fa";

export default function TestSummaryValidationDataType({warningIfSkipped}) {
    return (
        <>
            <Form.Item
                name={['data', 'passed']}
                label={
                    <Space>
                        <FaCheck color="green"/>
                        <Typography.Text>Number of passed tests</Typography.Text>
                    </Space>
                }
            >
                <InputNumber
                    min={0}
                />
            </Form.Item>
            <Form.Item
                name={['data', 'skipped']}
                label={
                    <Space>
                        <FaExclamationTriangle color="orange"/>
                        <Typography.Text>Number of skipped tests</Typography.Text>
                    </Space>
                }
            >
                <InputNumber
                    min={0}
                />
            </Form.Item>
            <Form.Item
                name={['data', 'failed']}
                label={
                    <Space>
                        <FaExclamationCircle color="red"/>
                        <Typography.Text>Number of failed tests</Typography.Text>
                    </Space>
                }
            >
                <InputNumber
                    min={0}
                />
            </Form.Item>
        </>
    )
}