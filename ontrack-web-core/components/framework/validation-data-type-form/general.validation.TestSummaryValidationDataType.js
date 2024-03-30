import {Form, Switch} from "antd";

export default function TestSummaryValidationDataType({prefix, warningIfSkipped}) {
    return (
        <>
            <Form.Item
                name={[prefix, "warningIfSkipped"]}
                extra="The validation will be flagged as a warning if some tests are skipped."
                label="Warnings if skipped"
            >
                <Switch/>
            </Form.Item>
        </>
    )
}