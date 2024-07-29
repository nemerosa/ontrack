import {Form, Input, InputNumber} from "antd";
import {prefixedFormName} from "@components/form/formUtils";
import SelectIssueService from "@components/extension/issues/SelectIssueService";

export default function PropertyForm({prefix}) {
    return (
        <>
            <Form.Item
                label="Configuration"
                extra="Name of the GitLab configuration in Ontrack"
                name={prefixedFormName(prefix, ['configuration', 'name'])}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                label="Repository"
                extra="GitLab repository, ie. project/name"
                name={prefixedFormName(prefix, 'repository')}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                label="Indexation interval"
                extra="How often to index the repository, in minutes. Use 0 to disable indexation."
                name={prefixedFormName(prefix, 'indexationInterval')}
            >
                <InputNumber min={0}/>
            </Form.Item>
            <Form.Item
                label="Issues"
                extra="Identifier for the issue service"
                name={prefixedFormName(prefix, 'issueServiceConfigurationIdentifier')}
            >
                <SelectIssueService
                    self="GitLab issues"
                />
            </Form.Item>
        </>
    )
}