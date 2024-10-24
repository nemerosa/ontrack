import {Form, Input} from "antd";

export default function EnvironmentRuleForm() {
    return (
        <>
            <Form.Item
                name={['ruleConfig', 'environmentName']}
                label="Environment name"
                rules={[
                    {
                        required: true,
                        message: 'Environment name is required.',
                    },
                ]}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                name={['ruleConfig', 'qualifier']}
                label="Slot qualifier"
            >
                <Input/>
            </Form.Item>
        </>
    )
}