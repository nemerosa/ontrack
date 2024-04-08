import {Form, Input} from "antd";

export default function WebhookNotificationChannelForm({prefix}) {
    return (
        <>
            <Form.Item
                name={[prefix, "name"]}
                label="Name"
                extra="Name of the webhook to call"
                rules={[{required: true, message: 'Name is required.'}]}
            >
                <Input/>
            </Form.Item>
        </>
    )
}