import {Form, Switch} from "antd";

export default function TestSummaryValidationDataType({prefix}) {
    return (
        <>
            <Form.Item
                name={[prefix, "warningIfSkipped"]}
                extra="The validation will be flagged as a warning if some tests are skipped."
                label="Warnings if skipped"
            >
                <Switch/>
            </Form.Item>
            <Form.Item
                name={[prefix, "failWhenNoResults"]}
                extra="The validation will be flagged as a failure if there are no tests."
                label="Failing if no tests"
            >
                <Switch/>
            </Form.Item>
        </>
    )
}