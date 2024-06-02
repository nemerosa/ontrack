import {Form, Switch} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function PropertyForm({prefix}) {

    return (
        <>
            <Form.Item
                label="Auto create"
                extra="If checked, creates validations from predefined ones"
                name={prefixedFormName(prefix, 'autoCreate')}
            >
                <Switch/>
            </Form.Item>
            <Form.Item
                label="Auto create if not predefined"
                extra="If checked, creates validations even if not predefined"
                name={prefixedFormName(prefix, 'autoCreateIfNotPredefined')}
            >
                <Switch/>
            </Form.Item>
        </>
    )
}