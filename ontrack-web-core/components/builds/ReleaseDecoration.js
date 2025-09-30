import {Space, Tag, Typography} from "antd";
import {FaCube} from "react-icons/fa";

export default function ReleaseDecoration({value}) {
    return <Tag color="green" title={
        `This build has been labeled/versioned/released with ${value}.`
    }>
        <Space>
            <FaCube/>
            <Typography.Text copyable>{value}</Typography.Text>
        </Space>

    </Tag>
}