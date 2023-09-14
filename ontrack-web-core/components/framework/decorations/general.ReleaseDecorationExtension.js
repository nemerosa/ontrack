import {Space, Tag} from "antd";
import {FaCube} from "react-icons/fa";

export default function ReleaseDecorationExtension({decoration}) {
    return <Tag color="green" title={
        `This build has been labeled/versioned/released with ${decoration.data}.`
    }>
        <Space>
            <FaCube/>
            {decoration.data}
        </Space>

    </Tag>
}