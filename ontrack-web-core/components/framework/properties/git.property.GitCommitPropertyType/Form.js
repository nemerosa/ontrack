import {Form, Input} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function PropertyForm({prefix}) {
    return (
        <>
            <Form.Item
                label="Commit"
                extra="Commit hash"
                name={prefixedFormName(prefix, 'commit')}
                rules={[{required: true, message: 'Commit is required.'}]}
            >
                <Input/>
            </Form.Item>
        </>
    )
}