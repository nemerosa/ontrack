import {Form, Input, InputNumber, Select, Switch} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function PropertyForm({prefix}) {

    return (
        <>
            <Form.Item
                label="Disabling after"
                extra="Number of days of inactivity after which the branch is disabled"
                name={prefixedFormName(prefix, 'disablingDuration')}
            >
                <InputNumber min={0}/>
            </Form.Item>
            <Form.Item
                label="Deleting after"
                extra="Number of days of inactivity after a branch has been disabled after which the branch is deleted. If 0, the branches are never deleted."
                name={prefixedFormName(prefix, 'deletingDuration')}
            >
                <InputNumber min={0}/>
            </Form.Item>
            <Form.Item
                label="Promotions to keep"
                extra="List of promotions to always keep. If a branch has at least one build having one of these promotions, the branch will never be disabled not deleted."
                name={prefixedFormName(prefix, 'promotionsToKeep')}
            >
                <Select mode="tags"/>
            </Form.Item>
            <Form.Item
                label="Includes"
                extra="Regular expression to identify branches which will never be disabled not deleted"
                name={prefixedFormName(prefix, 'includes')}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                label="Excludes"
                extra="Can define a regular expression for exceptions to the includes rule"
                name={prefixedFormName(prefix, 'excludes')}
            >
                <Input/>
            </Form.Item>
        </>
    )
}