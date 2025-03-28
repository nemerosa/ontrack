import {Form, Input, Switch} from "antd";

export default function ManualApprovalDataForm({configId, message}) {
    return (
        <>
            <Form.Item
                name={[configId, "approval"]}
                label="Approval"
            >
                <Switch data-testid="manual-approval"/>
            </Form.Item>
            <Form.Item
                name={[configId, "message"]}
                label={message}
            >
                <Input/>
            </Form.Item>
        </>
    )
}