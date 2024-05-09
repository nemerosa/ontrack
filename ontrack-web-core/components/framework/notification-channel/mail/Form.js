import {Form, Input} from "antd";

export default function MailNotificationChannelForm({prefix}) {
    return (
        <>
            <Form.Item
                name={[prefix, "to"]}
                label="Recipients (to)"
                extra="Comma-separated list of mail targets."
                rules={[{required: true, message: 'Recipient is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                name={[prefix, "cc"]}
                label="Recipients (cc)"
                extra="Comma-separated list of mail targets."
            >
                <Input/>
            </Form.Item>
            <Form.Item
                name={[prefix, "subject"]}
                label="Subject"
                extra="Subject template."
                rules={[{required: true, message: 'Subject is required.'}]}
            >
                <Input/>
            </Form.Item>
        </>
    )
}