import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form, InputNumber, Select, Switch} from "antd";

export default function ({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="disabled"
                    label="Disable collection"
                    extra="Check to disable the collection of SonarQube measures."
                >
                    <Switch/>
                </Form.Item>
                <Form.Item
                    name="measures"
                    label="Measures"
                    extra="List of SonarQube measures to export by default."
                >
                    <Select
                        mode="tags"
                        style={{width: '100%'}}
                        placeholder="Enter a list of measures."
                    />
                </Form.Item>
                <Form.Item
                    name="coverageThreshold"
                    label="Coverage threshold"
                    extra="Coverage to reach (in %)"
                >
                    <InputNumber min={0} max={100}/>
                </Form.Item>
                <Form.Item
                    name="blockerThreshold"
                    label="Blocker issues"
                    extra="Maximum number of blocker issues"
                >
                    <InputNumber min={1}/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}
