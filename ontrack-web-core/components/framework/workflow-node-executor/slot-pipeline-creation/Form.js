import {Form, Input} from "antd";

export default function SlotPipelineCreationWorkflowNodeExecutorForm() {
    return (
        <>
            <Form.Item
                name={['data', 'environment']}
                label="Environment"
                rules={[{required: true, message: 'Environment is required.'}]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                name={['data', 'qualifier']}
                label="Qualifier"
            >
                <Input/>
            </Form.Item>
        </>
    )

}