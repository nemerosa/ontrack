import {Form, Input} from "antd";
import SelectMessageType from "@components/common/SelectMessageType";
import {prefixedFormName} from "@components/form/formUtils";

export default function SlackNotificationChannelForm({prefix}) {
    return (
        <>
            <Form.Item
                name={prefixedFormName(prefix, 'channel')}
                label="Slack channel"
                rules={[{required: true, message: 'Slack channel is required.'}]}
            >
                <Input placeholder="#"/>
            </Form.Item>
            <Form.Item
                name={prefixedFormName(prefix, 'type')}
                label="Notification type"
                initialValue="INFO"
            >
                <SelectMessageType/>
            </Form.Item>
        </>
    )
}