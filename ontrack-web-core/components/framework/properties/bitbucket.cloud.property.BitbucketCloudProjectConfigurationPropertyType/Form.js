import {Form, Input, InputNumber} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function PropertyForm({prefix}) {

    return (
        <>
            <Form.Item
                label="Configuration"
                extra="Name of the Bitbucket Cloud configuration"
                name={prefixedFormName(prefix, 'configuration')}
                rules={[{required: true, message: 'Configuration is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                label="Repository"
                extra="Name of the repository"
                name={prefixedFormName(prefix, 'repository')}
                rules={[{required: true, message: 'Repository name is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                label="Indexation interval"
                extra="How often to index the repository, in minutes. Use 0 to disable indexation"
                name={prefixedFormName(prefix, 'indexationInterval')}
            >
                <InputNumber/>
            </Form.Item>
            <Form.Item
                label="Issue service"
                extra="Identifier for the issue service"
                name={prefixedFormName(prefix, 'issueServiceConfigurationIdentifier')}
            >
                <Input/>
            </Form.Item>
        </>
    )
}