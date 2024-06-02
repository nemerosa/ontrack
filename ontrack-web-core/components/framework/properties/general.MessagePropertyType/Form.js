import {Form, Input} from "antd";
import {prefixedFormName} from "@components/form/formUtils";
import SelectMessageType from "@components/common/SelectMessageType";

export default function PropertyForm({prefix}) {

    return (
        <>
            <Form.Item
                label="Type"
                extra="Type of message"
                name={prefixedFormName(prefix, 'type')}
            >
                <SelectMessageType/>
            </Form.Item>
            <Form.Item
                label="Text"
                extra="Content of the message"
                name={prefixedFormName(prefix, 'text')}
            >
                <Input/>
            </Form.Item>
        </>
    )
}