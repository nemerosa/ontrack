import {Descriptions, Space, Typography} from "antd";
import ProjectLink from "@components/projects/ProjectLink";

export default function SlotDetails({slot}) {

    const items = [
        {
            key: 'environment',
            label: 'Environment',
            children: slot.environment.name,
        },
        {
            key: 'description',
            label: 'Description',
            children: slot.description,
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