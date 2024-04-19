import {Form, Input} from "antd";

export default function PromotionLevelFormItemDescription() {
    return (
        <Form.Item
            name="description"
            label="Description"
            rules={[
                {
                    max: 500,
                    type: 'string',
                    message: 'Promotion level description must be 500 characters long at a maximum.',
                },
            ]}
        >
            <Input.TextArea placeholder="Promotion level description" allowClear/>
        </Form.Item>
    )
}