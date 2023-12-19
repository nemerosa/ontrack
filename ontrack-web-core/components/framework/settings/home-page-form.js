import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Alert, Form, InputNumber} from "antd";

export default function ({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item>
                    <Alert
                        type="warning"
                        message="Settings valid only for legacy (V4) UI. Will be removed in V5."
                    />
                </Form.Item>
                <Form.Item
                    name="maxBranches"
                    label="Max. branches"
                    extra="Maximum of branches to display per favorite project"
                >
                    <InputNumber min={0} max={100}/>
                </Form.Item>
                <Form.Item
                    name="maxProjects"
                    label="Max. project"
                    extra="Maximum of projects starting from which we need to switch to a search mode"
                >
                    <InputNumber min={0} max={100}/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}
