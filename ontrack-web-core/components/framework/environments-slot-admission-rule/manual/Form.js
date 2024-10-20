import {Form, Input} from "antd";

export default function ManualRuleForm() {
    return (
        <>
            <Form.Item
                name={['ruleConfig', 'message']}
                label="Message"
                rules={[{required: true, message: 'Message is required.'}]}
            >
                <Input/>
            </Form.Item>
            {/* TODO Users */}
            {/* TODO Groups */}
        </>
    )
}