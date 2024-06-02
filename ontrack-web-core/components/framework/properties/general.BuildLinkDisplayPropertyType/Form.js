import {Form, Switch} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function PropertyForm({prefix}) {

    return (
        <>
            <Form.Item
                label="Use label"
                extra={`Configuration at project label to specify that a
                    build link decoration should use the release/label
                    of a build when available. By default, it displays
                    the release name if available, and then the build name as a default.
                `}
                name={prefixedFormName(prefix, 'useLabel')}
            >
                <Switch/>
            </Form.Item>
        </>
    )
}