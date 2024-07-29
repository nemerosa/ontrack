import {Form, Input} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function PropertyForm({prefix}) {
    return (
        <>
            <Form.Item
                label="Configuration"
                extra="Name of the Git configuration in Ontrack"
                name={prefixedFormName(prefix, ['configuration', 'name'])}
            >
                <Input/>
            </Form.Item>
        </>
    )
}