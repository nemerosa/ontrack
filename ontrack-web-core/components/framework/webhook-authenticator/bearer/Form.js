import {Form, Input} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function BearerWebhookAuthenticatorForm({prefix, creation}) {
    return (
        <>
            <Form.Item
                name={prefixedFormName(prefix, 'token')}
                label="Token"
                extra="Token used to connect to the webhook"
                rules={[{required: creation, message: 'Token is required.'}]}
            >
                <Input.Password/>
            </Form.Item>
        </>
    )
}
