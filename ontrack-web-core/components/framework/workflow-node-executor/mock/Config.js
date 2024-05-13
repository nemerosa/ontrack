import {Descriptions, Typography} from "antd";
import DurationMs from "@components/common/DurationMs";

export default function MockWorkflowNodeExecutorConfig({data}) {

    const items = [
        {
            key: 'test',
            label: "Text",
            children: <Typography.Text>{data.text}</Typography.Text>
        },
        {
            key: 'waitMs',
            label: "Waiting time",
            children: <DurationMs ms={data.waitMs}/>
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