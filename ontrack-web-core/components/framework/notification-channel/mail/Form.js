import {Form, Input} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function MailNotificationChannelForm({prefix}) {
    return (
        <>
            <Form.Item
                name={prefixedFormName(prefix, 'to')}
                label="Recipients (to)"
                extra="Comma-separated list of mail targets."
                rules={[{required: true, message: 'Recipient is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                name={prefixedFormName(prefix, 'cc')}
                label="Recipients (cc)"
                extra="Comma-separated list of mail targets."
            >
                <Input/>
            </Form.Item>
            <Form.Item
                name={prefixedFormName(prefix, 'subject')}
                label="Subject"
                extra="Subject template."
                rules={[{required: true, message: 'Subject is required.'}]}
            >
                <Input/>
            </Form.Item>
        </>
    )
}