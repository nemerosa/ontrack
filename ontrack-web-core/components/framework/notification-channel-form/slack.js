import {Form, Input} from "antd";
import SelectMessageType from "@components/common/SelectMessageType";

export default function SlackNotificationChannelForm({prefix}) {
    return (
        <>
            <Form.Item
                name={[prefix, "channel"]}
                label="Slack channel"
            >
                <Input placeholder="#"/>
            </Form.Item>
            <Form.Item
                name={[prefix, "type"]}
                label="Notification type"
                initialValue="INFO"
            >
                <SelectMessageType/>
            </Form.Item>
        </>
    )
}