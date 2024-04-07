import {Select} from "antd";
import MessageType, {messageTypes} from "@components/common/MessageType";

export default function SelectMessageType({value, onChange}) {

    const options = messageTypes.map(name => ({
        value: name,
        label: <MessageType value={name}/>,
    }))

    return (
        <>
            <Select
                value={value}
                onChange={onChange}
                style={{width: '16em'}}
                options={options}
            />
        </>
    )
}