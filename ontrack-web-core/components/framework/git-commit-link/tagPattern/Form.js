import {Form, Input} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function TagPatternForm({prefix}) {
    return (
        <>
            <Form.Item
                name={prefixedFormName(prefix, 'pattern')}
                label="Pattern"
                extra="Regular expression to identity the tag. Uses the first capturing group to identify the build name."
            >
                <Input/>
            </Form.Item>
        </>
    )
}