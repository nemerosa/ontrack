import {Form, Input} from "antd";
import {prefixedFormName} from "@components/form/formUtils";
import SelectIssueService from "@components/extension/issues/SelectIssueService";

export default function PropertyForm({prefix}) {
    return (
        <>
            <Form.Item
                label="Name"
                name={prefixedFormName(prefix, 'name')}
                rules={[{required: true, message: 'Name is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                label="Issue service"
                extra="Identifier for the issue service"
                name={prefixedFormName(prefix, 'issueServiceIdentifier')}
            >
                <SelectIssueService self="Mock issues"/>
            </Form.Item>
        </>
    )
}