import {Form, InputNumber} from "antd";

export default function ThresholdNumberValidationDataType() {
    return (
        <>
            <Form.Item
                name={['data', 'value']}
                label="Value"
            >
                <InputNumber/>
            </Form.Item>
        </>
    )
}