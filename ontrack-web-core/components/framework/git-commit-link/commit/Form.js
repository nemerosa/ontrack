import {Form, Switch} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function CommitForm({prefix}) {
    return (
        <>
            <Form.Item
                name={prefixedFormName(prefix, 'abbreviated')}
                label="Abbreviated"
                extra="Using the abbrievated commit as the buid name."
            >
                <Switch/>
            </Form.Item>
        </>
    )
}