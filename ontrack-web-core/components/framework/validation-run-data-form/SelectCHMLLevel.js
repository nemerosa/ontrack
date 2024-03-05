import {Select} from "antd";

export default function SelectCHMLLevel({value, onChange, style}) {
    const values = [
        ['CRITICAL', 'Critical'],
        ['HIGH', 'High'],
        ['MEDIUM', 'Medium'],
        ['LOW', 'Low'],
    ]

    const options = values.map(([value, text]) => (
        {
            key: value,
            value: value,
            label: text,
        }
    ))

    return (
        <Select
            options={options}
            value={value}
            onChange={onChange}
            style={{width: '8em', ...style}}
        />
    )
}