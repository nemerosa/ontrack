import {Form, InputNumber} from "antd";

export default function FractionValidationDataType() {
    return (
        <>
            <Form.Item
                name={['data', 'numerator']}
                label="Numerator"
            >
                <InputNumber/>
            </Form.Item>
            <Form.Item
                name={['data', 'denominator']}
                label="Denominator"
            >
                <InputNumber/>
            </Form.Item>
        </>
    )
}