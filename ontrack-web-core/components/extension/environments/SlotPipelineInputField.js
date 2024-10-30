import InlineError from "@components/common/InlineError";
import {Input, Switch} from "antd";

export default function SlotPipelineInputField({id, type, value, onChange}) {
    switch (type) {
        case 'BOOLEAN':
            return <SlotPipelineInputFieldBoolean id={id} value={value} onChange={onChange}/>
        case 'TEXT':
            return <SlotPipelineInputFieldText id={id} value={value} onChange={onChange}/>
        default:
            return <InlineError message={`Input field type ${type} is unknown`}/>
    }
}

function SlotPipelineInputFieldBoolean({id, value, onChange}) {
    return (
        <>
            <Switch
                id={id}
                data-testid={id}
                value={value}
                onChange={onChange}
            />
        </>
    )
}

function SlotPipelineInputFieldText({id, value, onChange}) {
    return (
        <>
            <Input
                id={id}
                data-testid={id}
                value={value}
                onChange={onChange}
                rules={[
                    {
                        required: true,
                        message: "Field is required",
                    },
                ]}
            />
        </>
    )
}