import {Space, Typography} from "antd";
import {FaCheck} from "react-icons/fa";
import {useState} from "react";

export default function SelectableMenuItem({text, initialSelectedValue, onChange}) {
    const [selected, setSelected] = useState(initialSelectedValue)
    const onClick = () => {
        let newValue = !selected;
        setSelected(newValue)
        if (onChange) onChange(newValue)
    }
    return <Space onClick={onClick}>
        {
            selected ? <FaCheck/> : undefined
        }
        <Typography.Text>{text}</Typography.Text>
    </Space>
}