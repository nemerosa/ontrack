import {Space, Tag} from "antd";
import {FaCube} from "react-icons/fa";

export default function ReleaseDecorationExtension({decoration}) {
    return <Tag color="green">
        <Space>
            <FaCube/>
            {decoration.data}
        </Space>

    </Tag>
}