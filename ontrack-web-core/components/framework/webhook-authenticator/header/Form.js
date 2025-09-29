import {Form, Input} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function BearerWebhookAuthenticatorForm({prefix, creation}) {
    return (
        <>
            <Form.Item
                name={prefixedFormName(prefix, 'name')}
                label="Name"
                extra="Name of the header to send to the webhook"
                rules={[{required: true, message: 'Name is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                name={prefixedFormName(prefix, 'value')}
                label="Value"
                extra="Value of the header to send to the webhook"
                rules={[{required: creation, message: 'Value is required.'}]}
            >
                <Input.Password/>
            </Form.Item>
        </>
    )
}
