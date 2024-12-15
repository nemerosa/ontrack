import {Form, Select} from "antd";
import SettingsForm from "@components/core/admin/settings/SettingsForm";

export default function EnvironmentsForm({id, ...values}) {

    // See EnvironmentsSettingsBuildDisplayOption
    const options = [
        {value: 'ALL', label: 'All environments must be displayed'},
        {value: 'HIGHEST', label: 'Only the highest environment is displayed'},
        {value: 'COUNT', label: 'Only a count of the environments is displayed'},
    ]

    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="buildDisplayOption"
                    label="Display option for builds"
                >
                    <Select options={options}/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}