import {Descriptions} from "antd";

export default function WorkflowInstanceContext({instance}) {
    const items = instance.context.data.map(data => ({
        key: data.key,
        label: data.key,
        children: JSON.stringify(data.value),
    }))
    return (
        <>
            <Descriptions items={items}/>
        </>
    )
}