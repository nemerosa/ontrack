import {Space} from "antd";
import {FaCheck} from "react-icons/fa";

export default function SelectableMenuItem(
    {
        icon,
        text,
        value,
        onChange,
        extra,
    }
) {
    return (
        <Space>
            {/* Select icon or placeholder */}
            {value ? <FaCheck/> : undefined}
            {/* Menu body */}
            <Space onClick={onChange} className="ot-action">
                {icon}
                {text}
            </Space>
            {/* Extra (commands...) */}
            {extra}
        </Space>
    )
}