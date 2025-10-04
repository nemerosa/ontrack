import {Form, Input} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function PropertyForm({prefix}) {
    return (
        <>
            <Form.Item
                label="Validation"
                extra="Validation to set whenever the release/label property is set."
                name={prefixedFormName(prefix, "validation")}
                rules={[{required: true, message: 'Validation is required.'}]}
            >
                <Input/>
            </Form.Item>
        </>
    )
}