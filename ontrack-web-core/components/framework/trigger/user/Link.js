import {FaUser} from "react-icons/fa";
import {Popover} from "antd";

export default function UserTriggerLink({username}) {
    return (
        <>
            <Popover
                content={
                    `Triggered by ${username}`
                }
            >
                <FaUser/>
            </Popover>
        </>
    )
}