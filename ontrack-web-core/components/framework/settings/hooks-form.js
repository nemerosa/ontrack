import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form} from "antd";
import DurationPicker from "@components/common/DurationPicker";

export default function ({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="recordRetentionDuration"
                    label="Records retention"
                    extra="Maximum time to keep hook records for non-running requests"
                >
                    <DurationPicker/>
                </Form.Item>
                <Form.Item
                    name="recordCleanupDuration"
                    label="Records cleanup"
                    extra="Maximum time to keep queue records for all kinds of hook requests (counted _after_ the retention)"
                >
                    <DurationPicker/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}
