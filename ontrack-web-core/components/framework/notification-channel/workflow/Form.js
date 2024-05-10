import {Form, Input} from "antd";
import EditWorkflowButton from "@components/extension/workflows/EditWorkflowButton";
import {prefixedFormName} from "@components/form/formUtils";

export default function WorkflowNotificationChannelForm({prefix}) {

    return (
        <>
            <Form.Item
                name={prefixedFormName(prefix, 'workflow')}
                label="Workflow"
            >
                <EditWorkflowButton/>
            </Form.Item>
        </>
    )
}