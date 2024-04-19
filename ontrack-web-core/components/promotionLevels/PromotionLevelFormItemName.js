import {Form, Input} from "antd";

export default function PromotionLevelFormItemName() {
    return (
        <Form.Item
            name="name"
            label="Name"
            rules={[
                {required: true, message: 'Promotion level name is required.',},
                {
                    max: 40,
                    type: 'string',
                    message: 'Promotion level name must be 40 characters long at a maximum.',
                },
                {
                    pattern: /[A-Za-z0-9._-]+/,
                    message: 'Promotion level name must contain only letters, digits, dots, underscores or dashes.',
                },
            ]}
        >
            <Input placeholder="Promotion level name" allowClear/>
        </Form.Item>
    )
}