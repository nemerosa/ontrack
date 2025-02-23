import {FaVial} from "react-icons/fa";
import {Space, Typography} from "antd";

export default function TestTriggerComponent({message}) {
    return (
        <>
            <Space>
                <FaVial title="Test trigger"/>
                <Typography.Text>{message}</Typography.Text>
            </Space>
        </>
    )
}