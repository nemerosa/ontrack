import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form, Input, Switch} from "antd";

export default function ({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="enabled"
                    label="Enabled"
                    extra="Is Slack communication enabled?"
                >
                    <Switch/>
                </Form.Item>
                <Form.Item
                    name="token"
                    label="Token"
                    extra="Slack token"
                >
                    <Input.Password style={{width: '24em'}}/>
                </Form.Item>
                <Form.Item
                    name="emoji"
                    label="Emoji"
                    extra={
                        <>
                            Emoji (like <code>:ontrack:</code>) to use for the message
                        </>
                    }
                >
                    <Input style={{width: '16em'}}/>
                </Form.Item>
                <Form.Item
                    name="endpoint"
                    label="Endpoint"
                    extra="Slack API endpoint (leave blank for default)"
                >
                    <Input style={{width: '32em'}}/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}
