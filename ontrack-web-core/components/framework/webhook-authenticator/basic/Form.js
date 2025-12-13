import {Form, Input} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function BasicWebhookAuthenticatorForm({prefix, creation}) {
    return (
        <>
            <Form.Item
                name={prefixedFormName(prefix, 'username')}
                label="Username"
                extra="Username used to connect to the webhook"
                rules={[{required: true, message: 'Username is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                name={prefixedFormName(prefix, 'password')}
                label="Password"
                extra="Password used to connect to the webhook"
                rules={[{required: creation, message: 'Password is required.'}]}
            >
                <Input.Password/>
            </Form.Item>
        </>
    )
}
