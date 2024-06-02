import {DatePicker, Form, Input, InputNumber, Select} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function PropertyForm({prefix}) {

    return (
        <>
            <Form.Item
                label="Including branches"
                extra="List of regular expressions. AV requests match if at least one regular expression is matched by the target branch name. If empty, all target branches match (the default)."
                name={prefixedFormName(prefix, 'branchIncludes')}
            >
                <Select
                    mode="tags"
                    style={{width: '100%'}}
                    placeholder="Enter a list of regular expressions"
                />
            </Form.Item>
            <Form.Item
                label="Excluding branches"
                extra="List of regular expressions. AV requests match if no regular expression is matched by the target branch name. If empty, the target branch is considered matching."
                name={prefixedFormName(prefix, 'branchExcludes')}
            >
                <Select
                    mode="tags"
                    style={{width: '100%'}}
                    placeholder="Enter a list of regular expressions"
                />
            </Form.Item>
            <Form.Item
                label="Last activity date"
                extra="If defined, any target branch whose last activity (last build creation) is before this date will be ignored by the auto-versioning"
                name={prefixedFormName(prefix, 'lastActivityDate')}
            >
                <DatePicker showTime/>
            </Form.Item>
        </>
    )
}