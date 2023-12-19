import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form, Input, InputNumber, Switch} from "antd";

export default function ({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="enabled"
                    label="Ingestion enabled"
                    extra="Is the ingestion of the GitHub events enabled?"
                >
                    <Switch/>
                </Form.Item>
                <Form.Item
                    name="token"
                    label="Token"
                    extra="Secret token sent by the GitHub hook and signing the payload"
                >
                    <Input.Password/>
                </Form.Item>
                <Form.Item
                    name="retentionDays"
                    label="Retention days"
                    extra="Number of days to keep the received payloads (0 = forever)"
                >
                    <InputNumber min={1} max={365}/>
                </Form.Item>
                <Form.Item
                    name="orgProjectPrefix"
                    label="Org. project prefix"
                    extra="[on project creation] Must the organization name be used as a project name prefix?"
                >
                    <Switch/>
                </Form.Item>
                <Form.Item
                    name="indexationInterval"
                    label="Indexation interval (min)"
                    extra="[on project creation] Default indexation interval (in minutes) when configuring the GitHub projects"
                >
                    <InputNumber min={0}/>
                </Form.Item>
                <Form.Item
                    name="repositoryIncludes"
                    label="Include repositories"
                    extra="Regular expression to include repositories"
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="repositoryExcludes"
                    label="Exclude repositories"
                    extra="Regular expression to exclude repositories"
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="issueServiceIdentifier"
                    label="Default issue service identifier"
                    extra={
                        <>
                            Identifier of the issue service to use by default. For example <code>self</code> for GitHub
                            issues or <code>jira//config</code>.
                        </>
                    }
                >
                    <Input/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}
