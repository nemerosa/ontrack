import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form, Switch} from "antd";
import DurationPicker from "@components/common/DurationPicker";

export default function ({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="enabled"
                    label="Enabled"
                    extra="Is the recording of notifications enabled?"
                >
                    <Switch/>
                </Form.Item>
                <Form.Item
                    name="retentionSeconds"
                    label="Retention"
                    extra="Time to keep the recordings"
                >
                    <DurationPicker/>
                </Form.Item>
                <Form.Item
                    name="cleanupIntervalSeconds"
                    label="Cleanup interval"
                    extra="Interval between each cleanup of the recordings"
                >
                    <DurationPicker/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}
