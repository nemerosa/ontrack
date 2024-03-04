import {Form, InputNumber} from "antd";

export default function ThresholdPercentageValidationDataType() {
    return (
        <>
            <Form.Item
                name={['data', 'value']}
                label="Percentage"
            >
                <InputNumber
                    min={0}
                    max={100}
                />
            </Form.Item>
        </>
    )
}