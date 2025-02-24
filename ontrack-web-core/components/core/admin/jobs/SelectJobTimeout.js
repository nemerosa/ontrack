import {Select, Space, Typography} from "antd";
import {FaBan, FaTimesCircle} from "react-icons/fa";

export default function SelectJobTimeout({value, onChange, style, allowClear, placeholder}) {
    const options = [
        {
            value: "false",
            label: <Space>
                <Typography.Text type="secondary">
                    <FaBan/>
                </Typography.Text>
                Any timeout state
            </Space>
        },
        {
            value: "true",
            label: <Space>
                <Typography.Text type="danger">
                    <FaTimesCircle/>
                </Typography.Text>
                Job in timeout
            </Space>
        },
    ]

    return (
        <>
            <Select
                options={options}
                value={value}
                onChange={onChange}
                style={style}
                allowClear={allowClear}
                placeholder={placeholder}
            />
        </>
    )
}