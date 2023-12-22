import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form, Input, InputNumber, Typography} from "antd";

export default function GithubAvPostProcessingForm({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="config"
                    label="Configuration"
                    extra="Default GitHub configuration to use for the connection."
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="repository"
                    label="Repository"
                    extra={<>Default repository (like <code>owner/repository</code>) containing the workflow to run</>}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="workflow"
                    label="Workflow"
                    extra={<>Name of the workflow containing the post-processing
                        (like <code>post-processing.yml</code>)</>}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="branch"
                    label="Branch"
                    extra="Branch to launch for the workflow"
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="retries"
                    label="Retries"
                    extra="The amount of times we check for successful scheduling and completion of the post-processing job"
                >
                    <InputNumber min={0} max={50}/>
                </Form.Item>
                <Form.Item
                    name="retriesDelaySeconds"
                    label="Retry interval"
                    extra="The time (in seconds) between two checks for successful scheduling and completion of the post-processing job"
                >
                    <InputNumber min={5} max={300}/>
                </Form.Item>
            </SettingsForm>
        </>
    )
}
