import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form, Input} from "antd";

export default function LicenseForm({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="key"
                    label="License key"
                >
                    <Input.TextArea/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}
