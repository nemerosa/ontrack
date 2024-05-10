import {Form, Input} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function InMemoryNotificationChannelForm({prefix}) {
    return (
        <>
            <Form.Item
                name={prefixedFormName(prefix, 'group')}
                label="Group"
                extra="(only for test) Group of messages"
                rules={[{required: true, message: 'Group is required.'}]}
            >
                <Input placeholder="#"/>
            </Form.Item>
        </>
    )
}