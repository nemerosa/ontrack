import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form, InputNumber, Switch} from "antd";

export default function ({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="enabled"
                    label="Enabled"
                    extra="Check to enable the automated collection of labels for all projects. This can generate a high level activity in the background."
                >
                    <Switch/>
                </Form.Item>
                <Form.Item
                    name="interval"
                    label="Interval (min)"
                    extra="Interval (in minutes) between each label scan."
                >
                    <InputNumber min={1}/>
                </Form.Item>
                <Form.Item
                    name="perProject"
                    label="Per project"
                    extra="Check to have one distinct label collection job per project."
                >
                    <Switch/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}
