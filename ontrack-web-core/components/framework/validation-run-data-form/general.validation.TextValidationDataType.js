import {Form, Input} from "antd";

export default function TextValidationDataType() {
    return (
        <>
            <Form.Item
                name={['data', 'value']}
                label="Free text"
            >
                <Input/>
            </Form.Item>
        </>
    )
}