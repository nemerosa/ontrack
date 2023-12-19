import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form, Input, Switch} from "antd";

export default function ({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="syncEnabled"
                    label="Enabled"
                    extra="If synchronization of SCM catalog entries as Ontrack projects is enabled."
                >
                    <Switch/>
                </Form.Item>
                <Form.Item
                    name="orphanDisablingEnabled"
                    label="Orphan disabling"
                    extra="Automatically disables the projects which do not have a SCM entry any longer."
                >
                    <Switch/>
                </Form.Item>
                <Form.Item
                    name="scm"
                    label="SCM filter"
                    extra="Filter on the SCM type (regex)"
                >
                    <Input style={{width: '16em'}}/>
                </Form.Item>
                <Form.Item
                    name="config"
                    label="SCM config"
                    extra="Filter on the SCM config name (regex)"
                >
                    <Input style={{width: '16em'}}/>
                </Form.Item>
                <Form.Item
                    name="repository"
                    label="SCM repository"
                    extra="Filter on the SCM repository name (regex)"
                >
                    <Input style={{width: '16em'}}/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}
