import {Form, Input, InputNumber} from "antd";

export default function MockWorkflowNodeExecutorForm() {
    return (
        <>
            <Form.Item
                name={['data', 'text']}
                label="Text"
                rules={[{required: true, message: 'Text is required',},]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                name={['data', 'waitMs']}
                label="Waiting time"
                extra="Waiting time in milliseconds"
                rules={[{required: true, message: 'Waiting time is required',},]}
            >
                <InputNumber min={0}/>
            </Form.Item>
        </>
    )
}