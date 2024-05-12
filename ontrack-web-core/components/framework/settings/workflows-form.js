import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form} from "antd";
import DurationPicker from "@components/common/DurationPicker";

export default function WorkflowsForm({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="retentionDuration"
                    label="Retention duration"
                    extra="Duration before workflow instances are removed"
                >
                    <DurationPicker inMilliseconds={true}/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}