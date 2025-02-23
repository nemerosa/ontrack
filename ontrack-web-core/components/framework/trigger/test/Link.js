import {Popover} from "antd";
import {FaVial} from "react-icons/fa";

export default function TestTriggerLink({message}) {
    return (
        <>
            <Popover
                content={message}>
                <FaVial title="Test trigger"/>
            </Popover>
        </>
    )
}