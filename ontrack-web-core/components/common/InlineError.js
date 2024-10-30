import {Space, Tooltip, Typography} from "antd";
import {FaTimes} from "react-icons/fa";

export default function InlineError({message}) {
    return (
        <Tooltip title={message}>
            <Space>
                <FaTimes color="red"/>
                <Typography.Text>Error</Typography.Text>
            </Space>
        </Tooltip>
    )
}