import {Form, Input} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function PropertyForm({prefix}) {
    return (
        <>
            <Form.Item
                label="Name"
                name={prefixedFormName(prefix, 'name')}
                rules={[{required: true, message: 'Name is required.'}]}
            >
                <Input/>
            </Form.Item>
        </>
    )
}