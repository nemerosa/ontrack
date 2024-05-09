import {Descriptions, Typography} from "antd";

export default function MockWorkflowNodeExecutorConfig({data}) {

    const items = [
        {
            key: 'text',
            label: "Text",
            children: <Typography.Text>{data.text}</Typography.Text>
        },
    ]

    return (
        <>
            <Descriptions
                items={items}
            />
        </>
    )
}