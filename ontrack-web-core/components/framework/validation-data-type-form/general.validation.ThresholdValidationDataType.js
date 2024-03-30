import {Form, InputNumber, Switch} from "antd";

export default function ThresholdValidationDataType({prefix, ...config}) {
    return (
        <>
            <Form.Item
                name={[prefix, "warningThreshold"]}
                label="Warning"
                extra="Percentage to reach before having a warning. Optional."
                initialValue={config?.warningThreshold}
            >
                <InputNumber min={0} max={100}/>
            </Form.Item>
            <Form.Item
                name={[prefix, "failureThreshold"]}
                label="Failure"
                extra="Percentage to reach before having a failure. Optional."
                initialValue={config?.failureThreshold}
            >
                <InputNumber min={0} max={100}/>
            </Form.Item>
            <Form.Item
                name={[prefix, "okIfGreater"]}
                label="OK if greater"
                extra="If checked, greater values are better than lower ones."
                initialValue={config?.okIfGreater}
            >
                <Switch/>
            </Form.Item>
        </>
    )
}