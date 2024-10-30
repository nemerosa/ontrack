import {Card, Form} from "antd";
import SlotPipelineInputField from "@components/extension/environments/SlotPipelineInputField";

export default function SlotPipelineInput({input}) {
    return (
        <>
            <Card
                size="small"
                title={input.config.name}
            >
                {
                    input.fields.map((field, index) => (
                        <Form.Item
                            key={index}
                            name={[input.config.name, field.name]}
                            label={field.label}
                        >
                            <SlotPipelineInputField
                                type={field.type}
                            />
                        </Form.Item>
                    ))
                }
            </Card>
        </>
    )
}