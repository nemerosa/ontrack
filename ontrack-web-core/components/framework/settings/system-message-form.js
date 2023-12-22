import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form, Input} from "antd";
import SelectMessageType from "@components/common/SelectMessageType";

export default function SystemMessageForm({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="content"
                    label="Message content"
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="type"
                    label="Type"
                >
                    <SelectMessageType/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}