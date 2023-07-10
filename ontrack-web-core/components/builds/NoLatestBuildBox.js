import {Space, Typography} from "antd";
import {FaBan} from "react-icons/fa";

export default function NoLatestBuildBox() {
    return (
        <>
                <Space>
                    <FaBan/>
                    <Typography.Text type="secondary" italic>No build</Typography.Text>
                </Space>
        </>
    )
}