import {FaUser} from "react-icons/fa";
import {Space, Typography} from "antd";

export default function UserTriggerComponent({username}) {
    return (
        <>
            <Space>
                <FaUser/>
                <Typography.Text>Triggered by <b>{username}</b></Typography.Text>
            </Space>
        </>
    )
}