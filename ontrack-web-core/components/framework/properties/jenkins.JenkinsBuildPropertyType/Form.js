import {Form, Input, InputNumber} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function PropertyForm({prefix}) {
    return (
        <>
            <Form.Item
                label="Configuration"
                extra="Name of the Jenkins configuration in Ontrack"
                name={prefixedFormName(prefix, ['configuration', 'name'])}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                label="Job"
                extra="Path to the job in Jenkins"
                name={prefixedFormName(prefix, 'job')}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                label="Build"
                extra="Build number"
                name={prefixedFormName(prefix, 'build')}
            >
                <InputNumber min={1}/>
            </Form.Item>
        </>
    )
}