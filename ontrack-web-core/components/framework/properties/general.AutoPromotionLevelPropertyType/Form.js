import {Form, Switch} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function PropertyForm({prefix}) {

    return (
        <>
            <Form.Item
                label="Auto create"
                extra="If set, allows promotion levels to be created automatically"
                name={prefixedFormName(prefix, 'autoCreate')}
            >
                <Switch/>
            </Form.Item>
        </>
    )
}