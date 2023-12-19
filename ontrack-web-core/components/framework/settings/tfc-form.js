import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form, Input, Switch} from "antd";

export default function ({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="enabled"
                    label="Enabled"
                    extra="Is the support for TFC notifications enabled?"
                >
                    <Switch/>
                </Form.Item>
                <Form.Item
                    name="token"
                    label="Token"
                    extra="Secret token to be passed by TFC"
                >
                    <Input.Password style={{width: '36em'}}/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}
