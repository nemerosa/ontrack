import {Form, Input} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function WebhookNotificationChannelForm({prefix}) {
    return (
        <>
            <Form.Item
                name={prefixedFormName(prefix, 'name')}
                label="Name"
                extra="Name of the webhook to call"
                rules={[{required: true, message: 'Name is required.'}]}
            >
                <Input/>
            </Form.Item>
        </>
    )
}