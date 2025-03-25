import {Form, Input} from "antd";

export default function BuildNameFormItem() {
    return (
        <>
            <Form.Item name="name"
                       label="Name"
                       rules={[
                           {
                               required: true,
                               message: 'Build name is required.',
                           },
                           {
                               max: 150,
                               type: 'string',
                               message: 'Build name must be 150 characters long at a maximum.',
                           },
                           {
                               pattern: /[A-Za-z0-9._-]+/,
                               message: 'Build name must contain only letters, digits, dots, underscores or dashes.',
                           },
                       ]}
            >
                <Input placeholder="Build name" allowClear/>
            </Form.Item>
        </>
    )
}