import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form, InputNumber, Switch} from "antd";

export default function ({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="enabled"
                    label="Enabled"
                    extra="Are webhooks enabled?"
                >
                    <Switch/>
                </Form.Item>
                <Form.Item
                    name="timeoutMinutes"
                    label="Timeout (min)"
                    extra="Global timeout (in minutes) for all webhooks"
                >
                    <InputNumber min={1} max={60}/>
                </Form.Item>
                <Form.Item
                    name="deliveriesRetentionDays"
                    label="Delivery retention days"
                    extra="Retention time (in days) for the archiving of webhook deliveries"
                >
                    <InputNumber min={1}/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}
