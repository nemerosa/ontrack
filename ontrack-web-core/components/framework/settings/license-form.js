import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form, Input} from "antd";

export default function LicenseForm({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="license"
                    label="License"
                    extra="License key"
                >
                    <Input.TextArea/>
                </Form.Item>
                <Form.Item
                    name="signature"
                    label="Signature"
                    extra="Signature key"
                >
                    <Input.TextArea/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}
