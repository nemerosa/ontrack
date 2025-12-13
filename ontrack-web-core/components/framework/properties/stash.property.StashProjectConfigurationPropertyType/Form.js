import {Form, Input, InputNumber} from "antd";
import {prefixedFormName} from "@components/form/formUtils";
import SelectConfiguration from "@components/configurations/SelectConfiguration";
import SelectIssueService from "@components/extension/issues/SelectIssueService";

export default function PropertyForm({prefix}) {
    return (
        <>
            <Form.Item
                label="Configuration"
                extra="Name of the Bitbucket Server configuration in Yontrack"
                name={prefixedFormName(prefix, ['configuration', 'name'])}
                rules={[{required: true, message: 'Configuration is required.'}]}
            >
                <SelectConfiguration configurationType="bitbucket-server"/>
            </Form.Item>
            <Form.Item
                label="Project"
                extra="Name of the project in Bitbucket"
                name={prefixedFormName(prefix, 'project')}
                rules={[{required: true, message: 'Project is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                label="Repository"
                extra="Name of the repository in the project"
                name={prefixedFormName(prefix, 'repository')}
                rules={[{required: true, message: 'Repository is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                label="Indexation interval"
                extra="How often to index the repository, in minutes. Use 0 to disable indexation"
                name={prefixedFormName(prefix, 'indexationInterval')}
            >
                <InputNumber min={0}/>
            </Form.Item>
            <Form.Item
                label="Issue service"
                extra="Identifier for the issue service"
                name={prefixedFormName(prefix, 'issueServiceConfigurationIdentifier')}
            >
                <SelectIssueService/>
            </Form.Item>
        </>
    )
}