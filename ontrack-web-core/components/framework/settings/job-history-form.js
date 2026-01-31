import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form} from "antd";
import DurationPicker from "@components/common/DurationPicker";

export default function JobHistoryForm({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="retention"
                    label="Job history duration"
                    extra="Maximum duration to keep history for background jobs"
                >
                    <DurationPicker/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}