import SettingsForm from "@components/core/admin/settings/SettingsForm";
import {Form, Input, InputNumber} from "antd";

export default function JenkinsAutoVersioningProcessingForm({id, ...values}) {
    return (
        <>
            <SettingsForm id={id} values={values}>
                <Form.Item
                    name="config"
                    label="Configuration"
                    extra="Default Jenkins configuration to use for the connection"
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="job"
                    label="Job"
                    extra={
                        <>
                            Default path to the job to launch for the post-processing, relative to the Jenkins root URL
                            (note that <code>/job/</code> separators can be omitted)
                        </>
                    }
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
