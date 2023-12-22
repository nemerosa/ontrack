import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form, Select} from "antd";

export default function MainBuildLinksForm({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="labels"
                    label="Labels"
                    extra="List of project labels to keep as main dependencies"
                >
                    <Select
                        mode="tags"
                        style={{width: '100%'}}
                        placeholder="Enter a list of labels."
                    />
                </Form.Item>
            </SettingsForm>
        </>
    )
}