import {Form, Input} from "antd";

export default function BuildDescriptionFormItem() {
    return (
        <>
            <Form.Item
                name="description"
                label="Description"
                rules={[
                    {
                        max: 500,
                        type: 'string',
                        message: 'Build description must be 500 characters long at a maximum.',
                    },
                ]}
            >
                <Input placeholder="Build description" allowClear/>
            </Form.Item>
        </>
    )
}