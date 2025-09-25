import {Descriptions, Typography} from "antd";

export default function Display({property}) {

    const items = property.value.patterns.map((pattern) => ({
        key: pattern.name,
        label: pattern.name,
        children: <Typography.Text code>{pattern.value}</Typography.Text>,
        span: 12,
    }))

    return (
        <>
            <Descriptions items={items} column={12}/>
        </>
    )
}