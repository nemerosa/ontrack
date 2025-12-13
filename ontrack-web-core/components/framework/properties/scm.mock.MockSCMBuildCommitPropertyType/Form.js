import {Form, Input} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function PropertyForm({prefix}) {
    return (
        <>
            <Form.Item
                label="Commit"
                name={prefixedFormName(prefix, 'id')}
                rules={[{required: true, message: 'Commit is required.'}]}
            >
                <Input/>
            </Form.Item>
        </>
    )
}