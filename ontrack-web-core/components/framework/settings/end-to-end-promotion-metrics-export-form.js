import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form, Input, InputNumber, Switch} from "antd";

export default function EndToEndPromotionMetricsExportForm({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="enabled"
                    label="Enabled"
                    extra="Enabling the export of E2E promotion metrics."
                >
                    <Switch/>
                </Form.Item>
                <Form.Item
                    name="branches"
                    label="Branches"
                    extra="Regex for the branches eligible for the export. Valid for all projects."
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="pastDays"
                    label="Past days"
                    extra="Number of days in the past when looking for event metrics"
                >
                    <InputNumber min={1} max={60}/>
                </Form.Item>
                <Form.Item
                    name="restorationDays"
                    label="Restoration days"
                    extra="Number of days in the past to restore (used only for global restoration of data)"
                >
                    <InputNumber min={1} max={5000}/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}
