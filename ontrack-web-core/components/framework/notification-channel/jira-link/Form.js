import {Form, Input} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function JiraLinkNotificationChannelForm({prefix}) {
    return (
        <>
            <Form.Item
                name={prefixedFormName(prefix, 'configName')}
                label="Config"
                extra="Name of the Jira configuration to use for the connection."
                rules={[{required: true, message: 'Config name is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                name={prefixedFormName(prefix, 'sourceQuery')}
                label="Source query"
                extra="Jira query used to get the source ticket. It must return one and only one ticket. It can be a template."
                rules={[{required: true, message: 'Source query is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                name={prefixedFormName(prefix, 'targetQuery')}
                label="Target query"
                extra="Jira query used to get the target ticket. It must return one and only one ticket. It can be a template."
                rules={[{required: true, message: 'Target query is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                name={prefixedFormName(prefix, 'linkName')}
                label="Link name"
                extra="Name of the link type, like Relates, Blocks, etc."
                rules={[{required: true, message: 'Link name is required.'}]}
            >
                <Input/>
            </Form.Item>
        </>
    )
}