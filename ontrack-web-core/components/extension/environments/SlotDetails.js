import {Descriptions, Space, Typography} from "antd";
import ProjectLink from "@components/projects/ProjectLink";

export default function SlotDetails({slot}) {

    const items = [
        {
            key: 'environment',
            label: 'Environment',
            children: slot.environment.name,
            span: 6,
        },
        {
            key: 'project',
            label: 'Project',
            children: <Space>
                <ProjectLink project={slot.project}/>
                {
                    slot.qualifier &&
                    <Typography.Text>[{slot.qualifier}]</Typography.Text>
                }
            </Space>,
            span: 6,
        },
    ]
    if (slot.description) {
        items.push({
            key: 'description',
            label: 'Description',
            children: slot.description,
            span: 12,
        })
    }

    return (
        <>
            <Descriptions
                column={12}
                items={items}
            />
        </>
    )
}