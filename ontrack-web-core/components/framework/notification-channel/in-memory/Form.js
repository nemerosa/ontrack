import {Form, Input} from "antd";

export default function InMemoryNotificationChannelForm({prefix}) {
    return (
        <>
            <Form.Item
                name={[prefix, "group"]}
                label="Group"
                extra="(only for test) Group of messages"
                rules={[{required: true, message: 'Group is required.'}]}
            >
                <Input placeholder="#"/>
            </Form.Item>
        </>
    )
}