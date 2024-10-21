import {Form, Select} from "antd";

export default function BranchPatternRuleForm() {
    return (
        <>
            <Form.Item
                name={['ruleConfig', 'includes']}
                label="Includes regular expressions"
            >
                <Select
                    mode="tags"
                />
            </Form.Item>
            <Form.Item
                name={['ruleConfig', 'excludes']}
                label="Excludes regular expressions"
            >
                <Select
                    mode="tags"
                />
            </Form.Item>
        </>
    )
}