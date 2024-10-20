import {Form, Input} from "antd";

export default function PromotionRuleForm() {
    return (
        <>
            {/*  Promotion name  */}
            <Form.Item
                name={['ruleConfig', 'promotion']}
                label="Promotion name"
                rules={[{required: true, message: 'Promotion name is required.'}]}
            >
                <Input/>
            </Form.Item>
        </>
    )
}