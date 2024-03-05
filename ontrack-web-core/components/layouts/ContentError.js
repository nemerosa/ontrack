import {Space, Tooltip, Typography} from "antd";
import {FaTimes} from "react-icons/fa";

export default function ContentError() {
    return <Tooltip title="Could not render the page. This is a defect.">
        <Space>
            <FaTimes color="red"/>
            <Typography.Text>Error</Typography.Text>
        </Space>
    </Tooltip>
}