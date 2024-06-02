import {Form, Input, InputNumber} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function PropertyForm({prefix}) {

    return (
        <>
            <Form.Item
                label="Configuration"
                extra="Reference to the Artifactory configuration"
                name={prefixedFormName(prefix, 'configuration')}
                rules={[{required: true, message: 'Configuration is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                label="Build name"
                extra="Artifactory build name"
                name={prefixedFormName(prefix, 'buildName')}
                rules={[{required: true, message: 'Build name is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                label="Build name filter"
                extra="Artifactory build name filter"
                name={prefixedFormName(prefix, 'buildNameFilter')}
                rules={[{required: true, message: 'Build name filter is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                label="Interval"
                extra="Interval between each synchronisation in minutes"
                name={prefixedFormName(prefix, 'interval')}
                rules={[{required: true, message: 'Interval is required.'}]}
            >
                <InputNumber/>
            </Form.Item>
        </>
    )
}